package com.moyoy.api.pr_review.application;

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
	public void increaseHitAsync(Long reviewId, Long userId, String clientIp) {

		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(reviewId, userId, clientIp));

		if (!prReviewHitRepository.saveIfAbsent(hit))
			return;

		prReviewRepository.increaseHitCount(reviewId);
	}
}
