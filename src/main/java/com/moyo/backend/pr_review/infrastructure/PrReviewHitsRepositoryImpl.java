package com.moyo.backend.pr_review.infrastructure;

import org.springframework.stereotype.Repository;

import com.moyo.backend.pr_review.application.port.PrReviewHitsRepository;
import com.moyo.backend.pr_review.domain.PrReview;
import com.moyo.backend.pr_review.domain.PrReviewHits;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PrReviewHitsRepositoryImpl implements PrReviewHitsRepository {

	private final PrReviewHitsJpaRepository prReviewHitsJpaRepository;

	@Override
	public boolean existsByPrReviewIdAndUserId(Long prReviewRequestId, Long userId) {
		return prReviewHitsJpaRepository.existsByPrReviewIdAndUserId(prReviewRequestId, userId);
	}

	@Override
	public void deleteByPrReview(PrReview prReview) {
		prReviewHitsJpaRepository.deleteByPrReview(prReview);
	}

	@Override
	public void save(PrReviewHits prReviewHits) {
		prReviewHitsJpaRepository.save(prReviewHits);
	}
}
