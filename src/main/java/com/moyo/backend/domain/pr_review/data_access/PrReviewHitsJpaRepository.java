package com.moyo.backend.domain.pr_review.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyo.backend.domain.pr_review.implement.PrReview;
import com.moyo.backend.domain.pr_review.implement.PrReviewHits;

public interface PrReviewHitsJpaRepository extends JpaRepository<PrReviewHits, Long> {

	boolean existsByPrReviewIdAndUserId(Long prReviewRequestId, Long userId);

	void deleteByPrReview(PrReview prReview);
}
