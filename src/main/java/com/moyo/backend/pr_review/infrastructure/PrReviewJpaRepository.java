package com.moyo.backend.pr_review.infrastructure;

import com.moyo.backend.pr_review.domain.PrReview;
import com.moyo.backend.pr_review.domain.position.Position;
import com.moyo.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrReviewJpaRepository extends JpaRepository<PrReview, Long> {

    @Query("SELECT pr FROM PrReview pr WHERE pr.status = :status AND (:position IS NULL OR pr.position = :position)")
    Page<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

    @Query("SELECT pr FROM PrReview pr WHERE pr.user = :user AND pr.status = :status AND (:position IS NULL OR pr.position = :position)")
    Page<PrReview> findAllByUserAndStatusAndPosition(User user, Boolean status, Position position, Pageable pageable);
}
