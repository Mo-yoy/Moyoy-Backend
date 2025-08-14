package com.moyoy.core.db_access.domain.pr_review;

import com.moyoy.core.domain.pr_review.implement.PrReview;
import com.moyoy.core.domain.pr_review.implement.PrReviewHits;

public interface PrReviewHitsRepository {

	boolean existsByPrReviewIdAndUserId(Long prReviewRequestId, Long userId);

	void deleteByPrReview(PrReview prReview);

	void save(PrReviewHits prReviewHits);
}
