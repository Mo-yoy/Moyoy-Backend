package com.moyo.backend.domain.pr_review.data_access;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.moyo.backend.domain.pr_review.implement.Position;
import com.moyo.backend.domain.pr_review.implement.PrReview;

public interface PrReviewRepository {

	Slice<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

	Slice<PrReview> findAllByUserIdAndStatusAndPosition(Long userId, Boolean status, Position position, Pageable pageable);

	Optional<PrReview> findById(Long reviewId);

	PrReview save(PrReview review);

	void delete(PrReview review);
}
