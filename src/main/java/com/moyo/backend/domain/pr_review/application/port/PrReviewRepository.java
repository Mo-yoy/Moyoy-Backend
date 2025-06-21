package com.moyo.backend.domain.pr_review.application.port;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.moyo.backend.domain.pr_review.domain.PrReview;
import com.moyo.backend.domain.pr_review.domain.position.Position;
import com.moyo.backend.domain.user.domain.User;

public interface PrReviewRepository {

	Page<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

	Page<PrReview> findAllByUserAndStatusAndPosition(User user, Boolean status, Position position, Pageable pageable);

	Optional<PrReview> findById(Long reviewId);

	PrReview save(PrReview review);

	void delete(PrReview review);
}
