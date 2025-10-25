package com.moyoy.domain.pr_review;

import java.util.Optional;

public interface PrReviewRepository {

	PrReview save(PrReview newPrReview);

	Optional<PrReview> findById(Long reviewId);

	void deleteById(Long reviewId);
}
