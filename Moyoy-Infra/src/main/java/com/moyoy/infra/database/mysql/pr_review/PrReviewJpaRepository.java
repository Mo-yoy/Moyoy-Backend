package com.moyoy.infra.database.mysql.pr_review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrReviewJpaRepository extends JpaRepository<PrReviewEntity, Long> {

	@Modifying
	@Query("UPDATE PrReviewEntity r SET r.hitCount = r.hitCount + 1 WHERE r.id = :reviewId")
	void increaseHitCount(@Param("reviewId") Long reviewId);
}
