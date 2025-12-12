package com.moyoy.domain.pr_review;

import java.time.LocalDateTime;

public interface PrReviewHitRepository {
	PrReviewHit saveIfNotExist(PrReviewHit prReviewHit);

	void updateLastIncreasedAt(Long id, LocalDateTime now);
}
