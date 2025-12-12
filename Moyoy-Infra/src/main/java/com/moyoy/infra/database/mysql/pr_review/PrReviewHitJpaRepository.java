package com.moyoy.infra.database.mysql.pr_review;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrReviewHitJpaRepository extends JpaRepository<PrReviewHitEntity, Long> {

	PrReviewHitEntity findByPrReviewIdAndUserId(Long prReviewId, Long userId);

	@Modifying
	@Query("UPDATE PrReviewHitEntity h SET h.lastIncreasedAt = :now WHERE h.id = :id")
	void updateLastIncreasedAt(@Param("id") Long id, @Param("now") LocalDateTime now);
}
