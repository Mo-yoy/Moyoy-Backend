package com.moyo.backend.domain.pr_review.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyo.backend.domain.pr_review.domain.PrReview;
import com.moyo.backend.domain.pr_review.domain.PrReviewHits;

@Repository
public interface PrReviewHitsJpaRepository extends JpaRepository<PrReviewHits, Long> {

	boolean existsByPrReviewIdAndUserId(Long prReviewRequestId, Long userId);

	void deleteByPrReview(PrReview prReview);
}
