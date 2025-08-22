package com.moyoy.infra.database.pr_review;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PrReviewJpaRepository extends JpaRepository<PrReviewEntity, Long> {

	@Query("SELECT pr FROM PrReviewEntity pr WHERE pr.status = :status AND (:position IS NULL OR pr.position = :position)")
	Slice<PrReviewEntity> findAllByStatusAndPosition(Status status, Position position, Pageable pageable);

	// 동등 조건 선행 쿼리 튜닝 ← user id 변경 적용 후 리팩토링 예정.
	@Query("SELECT pr FROM PrReviewEntity pr WHERE pr.userId = :userId AND pr.status = :status AND (:position IS NULL OR pr.position = :position)")
	Slice<PrReviewEntity> findAllByUserIdAndStatusAndPosition(Long userId, Status status, Position position, Pageable pageable);

	@Modifying
	@Query("DELETE FROM PrReviewEntity pr WHERE pr.id = :reviewId")
	void deleteById(@NonNull Long reviewId);
}