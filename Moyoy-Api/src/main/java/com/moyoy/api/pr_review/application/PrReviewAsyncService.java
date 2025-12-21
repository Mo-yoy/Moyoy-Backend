package com.moyoy.api.pr_review.application;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moyoy.domain.pr_review.PrReviewHit;
import com.moyoy.domain.pr_review.PrReviewHitRepository;
import com.moyoy.domain.pr_review.PrReviewRepository;
import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

@Service
@RequiredArgsConstructor
public class PrReviewAsyncService {

	private final PrReviewRepository prReviewRepository;
	private final PrReviewHitRepository prReviewHitRepository;

	@Async("hitsExecutor")
	@Transactional
	public void increaseHitAsync(Long reviewId, Long userId) {
		/// 해당 트랜잭션이 실패하면, 후처리를 할 것인가.

		LocalDateTime now = LocalDateTime.now();

		PrReviewHit hit = prReviewHitRepository.findOrCreate(
			PrReviewHit.create(new PrReviewHitCreate(reviewId, userId, now)));

		if (!hit.canIncrease(now))
			return;

		prReviewRepository.increaseHitCount(reviewId);

		prReviewHitRepository.updateLastIncreasedAt(hit.getId(), now);
	}
}
