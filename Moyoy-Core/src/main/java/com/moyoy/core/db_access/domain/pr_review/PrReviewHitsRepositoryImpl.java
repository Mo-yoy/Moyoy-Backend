package com.moyoy.core.db_access.domain.pr_review;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.moyoy.core.domain.pr_review.implement.PrReview;
import com.moyoy.core.domain.pr_review.implement.PrReviewHits;

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
