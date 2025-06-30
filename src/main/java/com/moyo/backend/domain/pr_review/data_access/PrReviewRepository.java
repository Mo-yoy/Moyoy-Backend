package com.moyo.backend.domain.pr_review.data_access;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.moyo.backend.domain.pr_review.implement.PrReview;
import com.moyo.backend.domain.pr_review.implement.Position;
import com.moyo.backend.domain.user.implement.User;

public interface PrReviewRepository {

	Page<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

	Page<PrReview> findAllByUserAndStatusAndPosition(User user, Boolean status, Position position, Pageable pageable);

	Optional<PrReview> findById(Long reviewId);

	PrReview save(PrReview review);

	void delete(PrReview review);
}
