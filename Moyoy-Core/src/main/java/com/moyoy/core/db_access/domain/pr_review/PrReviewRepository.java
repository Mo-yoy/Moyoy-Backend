package com.moyoy.core.db_access.domain.pr_review;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.moyoy.core.domain.pr_review.implement.Position;
import com.moyoy.core.domain.pr_review.implement.PrReview;

public interface PrReviewRepository {

	Slice<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

	Slice<PrReview> findAllByUserIdAndStatusAndPosition(Long userId, Boolean status, Position position, Pageable pageable);

	Optional<PrReview> findById(Long reviewId);

	PrReview save(PrReview review);

	void delete(PrReview review);
}
