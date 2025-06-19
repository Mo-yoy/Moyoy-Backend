package com.moyo.backend.pr_review.application.port;

import com.moyo.backend.pr_review.domain.PrReview;
import com.moyo.backend.pr_review.domain.position.Position;
import com.moyo.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PrReviewRepository {

    Page<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

    Page<PrReview> findAllByUserAndStatusAndPosition(User user, Boolean status, Position position, Pageable pageable);

    Optional<PrReview> findById(Long reviewId);

    PrReview save(PrReview review);

    void delete(PrReview review);
}
