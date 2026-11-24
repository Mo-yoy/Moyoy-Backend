package com.moyoy.infra.redis.pr_review;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.PrReviewHitCount;
import com.moyoy.domain.pr_review.PrReviewHitRepository;

@Repository
@RequiredArgsConstructor
public class PrReviewHitRepositoryImpl implements PrReviewHitRepository {

	/// pr_review_hit::{구간}::{요청글 ID} : 구간별 고유 조회자 (HyperLogLog)
	private static final String HIT_KEY_PREFIX = "pr_review_hit::";
	/// pr_review_hit_window::{구간} : 구간 내 조회된 요청글 ID 목록 (Set)
	private static final String WINDOW_KEY_PREFIX = "pr_review_hit_window::";

	private static final DateTimeFormatter WINDOW_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHH");

	/// 서버 간 시각 차이로 끝난 구간에 늦게 기록되는 조회까지 포함하도록 구간 종료 후 잠시 기다렸다가 반영
	private static final Duration CLOSE_DELAY = Duration.ofMinutes(1);

	/// Batch가 멈춰 반영되지 못한 키가 메모리에 계속 남지 않도록 하는 안전장치
	private static final Duration KEY_TTL = Duration.ofDays(1);

	/// PFADD, SADD는 키를 새로 만들면 항상 1을 반환하므로, 그때만 TTL을 걸어 중복 조회마다 EXPIRE가 나가지 않게 함
	private static final RedisScript<Long> SAVE_SCRIPT = RedisScript.of(
		"if redis.call('PFADD', KEYS[1], ARGV[1]) == 1 then redis.call('EXPIRE', KEYS[1], ARGV[3]) end "
			+ "if redis.call('SADD', KEYS[2], ARGV[2]) == 1 then redis.call('EXPIRE', KEYS[2], ARGV[3]) end "
			+ "return 1",
		Long.class);

	private final StringRedisTemplate stringRedisTemplate;

	/// HyperLogLog 기록, 구간 목록 추가, TTL 설정을 한 번의 왕복으로 처리
	@Override
	public void save(PrReviewHit prReviewHit) {

		String window = prReviewHit.getWindowStart().format(WINDOW_FORMAT);

		stringRedisTemplate.execute(
			SAVE_SCRIPT,
			List.of(HIT_KEY_PREFIX + window + "::" + prReviewHit.getPrReviewId(), WINDOW_KEY_PREFIX + window),
			prReviewHit.getViewerKey(),
			String.valueOf(prReviewHit.getPrReviewId()),
			String.valueOf(KEY_TTL.getSeconds()));
	}

	@Override
	public List<PrReviewHitCount> popClosedWindowCounts(LocalDateTime now) {

		Map<Long, Integer> countsByReviewId = new LinkedHashMap<>();

		for (String window : findClosedWindows(now)) {
			Set<String> reviewIds = stringRedisTemplate.opsForSet().members(WINDOW_KEY_PREFIX + window);

			for (String reviewId : reviewIds) {
				String hitKey = HIT_KEY_PREFIX + window + "::" + reviewId;
				Long uniqueViewers = stringRedisTemplate.opsForHyperLogLog().size(hitKey);
				countsByReviewId.merge(Long.valueOf(reviewId), uniqueViewers.intValue(), Integer::sum);
				stringRedisTemplate.delete(hitKey);
			}
			stringRedisTemplate.delete(WINDOW_KEY_PREFIX + window);
		}

		List<PrReviewHitCount> hitCounts = new ArrayList<>();
		countsByReviewId.forEach((reviewId, count) -> hitCounts.add(PrReviewHitCount.builder()
			.prReviewId(reviewId)
			.count(count)
			.build()));
		return hitCounts;
	}

	private List<String> findClosedWindows(LocalDateTime now) {

		List<String> closedWindows = new ArrayList<>();
		ScanOptions options = ScanOptions.scanOptions().match(WINDOW_KEY_PREFIX + "*").build();

		try (Cursor<String> cursor = stringRedisTemplate.scan(options)) {
			cursor.forEachRemaining(key -> {
				String window = key.substring(WINDOW_KEY_PREFIX.length());
				LocalDateTime windowEnd = LocalDateTime.parse(window, WINDOW_FORMAT).plus(PrReviewHit.WINDOW);
				if (!windowEnd.plus(CLOSE_DELAY).isAfter(now)) {
					closedWindows.add(window);
				}
			});
		}
		return closedWindows;
	}
}
