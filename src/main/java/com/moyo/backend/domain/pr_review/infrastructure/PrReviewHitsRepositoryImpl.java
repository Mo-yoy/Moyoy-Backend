package com.moyo.backend.domain.pr_review.infrastructure;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.moyo.backend.domain.pr_review.application.port.PrReviewHitsRepository;
import com.moyo.backend.domain.pr_review.domain.PrReview;
import com.moyo.backend.domain.pr_review.domain.PrReviewHits;

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
