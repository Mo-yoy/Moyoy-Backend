package com.moyo.backend.domain.pr_review.data_access;

import com.moyo.backend.domain.pr_review.implement.PrReview;
import com.moyo.backend.domain.pr_review.implement.PrReviewHits;

public interface PrReviewHitsRepository {

	boolean existsByPrReviewIdAndUserId(Long prReviewRequestId, Long userId);

	void deleteByPrReview(PrReview prReview);

	void save(PrReviewHits prReviewHits);
}
