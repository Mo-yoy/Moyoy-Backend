package com.moyoy.infra.redis.pr_review;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.PrReviewHitRepository;

@Repository
@RequiredArgsConstructor
public class PrReviewHitRepositoryImpl implements PrReviewHitRepository {

	private static final String KEY_PREFIX = "pr_review_hit::";

	private final StringRedisTemplate stringRedisTemplate;

	/// SET NX EX: 키가 없을 때만 기록하고, 키마다 TTL이 걸려 조회자별로 중복 판단 기간을 관리
	@Override
	public boolean saveIfAbsent(PrReviewHit prReviewHit) {

		String key = KEY_PREFIX + prReviewHit.getPrReviewId() + "::" + prReviewHit.getViewerKey();

		Boolean saved = stringRedisTemplate.opsForValue()
			.setIfAbsent(key, "1", PrReviewHit.DUPLICATE_WINDOW);

		return Boolean.TRUE.equals(saved);
	}
}
