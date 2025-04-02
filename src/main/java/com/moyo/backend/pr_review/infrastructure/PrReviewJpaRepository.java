package com.moyo.backend.pr_review.infrastructure;

import com.moyo.backend.pr_review.domain.PrReview;
import com.moyo.backend.pr_review.domain.position.Position;
import com.moyo.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrReviewJpaRepository extends JpaRepository<PrReview, Long> {

    Page<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

    Page<PrReview> findAllByUserAndStatusAndPosition(User user, Boolean status, Position position, Pageable pageable);
}
