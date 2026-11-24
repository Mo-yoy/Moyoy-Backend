package com.moyoy.infra.redis.pr_review;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.moyoy.domain.pr_review.PrReviewHitCount;
import com.moyoy.domain.pr_review.PrReviewHitCountRepository;

@Repository
@RequiredArgsConstructor
public class PrReviewHitCountRepositoryImpl implements PrReviewHitCountRepository {

	private static final String COUNT_KEY_PREFIX = "pr_review_hit_count::";
	private static final String UPDATED_REVIEW_IDS_KEY = "pr_review_hit_count::updated";

	private final StringRedisTemplate stringRedisTemplate;

	/// 증가분을 먼저 누적한 뒤 갱신 목록에 추가해야, 반영 중에 들어온 증가분도 다음 반영 때 빠지지 않음
	@Override
	public void increase(Long prReviewId) {

		stringRedisTemplate.opsForValue().increment(COUNT_KEY_PREFIX + prReviewId);
		stringRedisTemplate.opsForSet().add(UPDATED_REVIEW_IDS_KEY, String.valueOf(prReviewId));
	}

	/// 시작 시점의 갱신 목록 크기만큼만 꺼내서, 반영 중에 계속 들어오는 요청 때문에 끝나지 않는 일이 없게 함
	@Override
	public List<PrReviewHitCount> popAll() {

		Long size = stringRedisTemplate.opsForSet().size(UPDATED_REVIEW_IDS_KEY);
		if (size == null || size == 0) {
			return List.of();
		}

		List<String> reviewIds = stringRedisTemplate.opsForSet().pop(UPDATED_REVIEW_IDS_KEY, size);
		if (reviewIds == null) {
			return List.of();
		}

		List<PrReviewHitCount> hitCounts = new ArrayList<>();
		for (String reviewId : reviewIds) {
			String count = stringRedisTemplate.opsForValue().getAndDelete(COUNT_KEY_PREFIX + reviewId);
			if (count == null) {
				continue;
			}
			hitCounts.add(PrReviewHitCount.builder()
				.prReviewId(Long.valueOf(reviewId))
				.count(Integer.parseInt(count))
				.build());
		}
		return hitCounts;
	}

	@Override
	public void restore(PrReviewHitCount hitCount) {

		stringRedisTemplate.opsForValue().increment(COUNT_KEY_PREFIX + hitCount.getPrReviewId(), hitCount.getCount());
		stringRedisTemplate.opsForSet().add(UPDATED_REVIEW_IDS_KEY, String.valueOf(hitCount.getPrReviewId()));
	}
}
