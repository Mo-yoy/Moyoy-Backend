package com.moyo.backend.domain.pr_review.application.port;

import com.moyo.backend.domain.pr_review.domain.PrReview;
import com.moyo.backend.domain.pr_review.domain.PrReviewHits;

public interface PrReviewHitsRepository {

	boolean existsByPrReviewIdAndUserId(Long prReviewRequestId, Long userId);

	void deleteByPrReview(PrReview prReview);

	void save(PrReviewHits prReviewHits);
}
