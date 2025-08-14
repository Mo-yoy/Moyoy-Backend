package com.moyoy.core.db_access.domain.pr_review;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyoy.core.domain.pr_review.implement.PrReview;
import com.moyoy.core.domain.pr_review.implement.PrReviewHits;

public interface PrReviewHitsJpaRepository extends JpaRepository<PrReviewHits, Long> {

	boolean existsByPrReviewIdAndUserId(Long prReviewRequestId, Long userId);

	void deleteByPrReview(PrReview prReview);
}
