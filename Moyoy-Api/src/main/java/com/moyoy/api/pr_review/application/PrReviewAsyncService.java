package com.moyoy.api.pr_review.application;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.PrReviewHitRepository;
import com.moyoy.domain.pr_review.PrReviewRepository;
import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

@Service
@RequiredArgsConstructor
public class PrReviewAsyncService {

	private final PrReviewRepository prReviewRepository;
	private final PrReviewHitRepository prReviewHitRepository;

	/// 중복 조회는 Redis에서 걸러내고 DB 트랜잭션을 열지 않음 (트랜잭션은 increaseHitCount 쿼리에만)
	@Async("hitsExecutor")
	public void increaseHitAsync(Long reviewId, Long userId, String clientIp) {

		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(reviewId, userId, clientIp));

		if (!prReviewHitRepository.saveIfAbsent(hit)) {
			return;
		}

		prReviewRepository.increaseHitCount(reviewId);
	}
}
