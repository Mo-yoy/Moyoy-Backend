package com.moyoy.api.pr_review.application;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.PrReviewHitCountRepository;
import com.moyoy.domain.pr_review.PrReviewHitRepository;
import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

@Service
@RequiredArgsConstructor
public class PrReviewAsyncService {

	private final PrReviewHitRepository prReviewHitRepository;
	private final PrReviewHitCountRepository prReviewHitCountRepository;

	/// 조회수 증가분은 Redis에 누적하고, RDB 반영은 Batch에서 주기적으로 일괄 처리
	@Async("hitsExecutor")
	public void increaseHitAsync(Long reviewId, Long userId, String clientIp) {

		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(reviewId, userId, clientIp));

		if (!prReviewHitRepository.saveIfAbsent(hit)) {
			return;
		}

		prReviewHitCountRepository.increase(reviewId);
	}
}
