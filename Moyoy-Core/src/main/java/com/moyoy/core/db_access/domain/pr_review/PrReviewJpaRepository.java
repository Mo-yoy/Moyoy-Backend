package com.moyoy.core.db_access.domain.pr_review;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.moyoy.core.domain.pr_review.implement.Position;
import com.moyoy.core.domain.pr_review.implement.PrReview;

public interface PrReviewJpaRepository extends JpaRepository<PrReview, Long> {

	@Query("SELECT pr FROM PrReview pr WHERE pr.opened = :status AND (:position IS NULL OR pr.position = :position)")
	Slice<PrReview> findAllByStatusAndPosition(Boolean status, Position position, Pageable pageable);

	// 동등 조건 선행 쿼리 튜닝 ← user id 변경 적용 후 리팩토링 예정.
	@Query("SELECT pr FROM PrReview pr WHERE pr.user.id = :userId AND pr.opened = :status AND (:position IS NULL OR pr.position = :position)")
	Slice<PrReview> findAllByUserIdAndStatusAndPosition(Long userId, Boolean status, Position position, Pageable pageable);
}
