package com.moyo.backend.domain.pr_review.data_access;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.moyo.backend.domain.pr_review.implement.PrReview;
import com.moyo.backend.domain.pr_review.implement.Position;
import com.moyo.backend.domain.user.implement.User;

public interface PrReviewJpaRepository extends JpaRepository<PrReview, Long> {

	@Query("SELECT pr FROM PrReview pr WHERE pr.status = :status AND (:position IS NULL OR pr.position = :position)")
	Page<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

	@Query("SELECT pr FROM PrReview pr WHERE pr.user = :user AND pr.status = :status AND (:position IS NULL OR pr.position = :position)")
	Page<PrReview> findAllByUserAndStatusAndPosition(User user, Boolean status, Position position, Pageable pageable);
}
