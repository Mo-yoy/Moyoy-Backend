package com.moyo.backend.domain.pr_review.data_access;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.moyo.backend.domain.pr_review.implement.Position;
import com.moyo.backend.domain.pr_review.implement.PrReview;

public interface PrReviewJpaRepository extends JpaRepository<PrReview, Long> {

	@Query("SELECT pr FROM PrReview pr WHERE pr.status = :status AND (:position IS NULL OR pr.position = :position)")
	Slice<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

	@Query("SELECT pr FROM PrReview pr WHERE pr.user.id = :userId AND pr.status = :status AND (:position IS NULL OR pr.position = :position)")
	Slice<PrReview> findAllByUserIdAndStatusAndPosition(Long userId, Boolean status, Position position, Pageable pageable);
}
