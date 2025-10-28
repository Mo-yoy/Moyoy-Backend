package com.moyoy.infra.database.mysql.pr_review.request;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;

public record PrReviewQueryConditionData(
	Long userId,
	Status status,
	String order,
	Position position,
	Long lastReviewId,
	int size) {
	public PrReviewQueryConditionData of(Long userId, Status status, String order, Position position, Long lastReviewId, int size) {
		return new PrReviewQueryConditionData(userId, status, order, position, lastReviewId, size);
	}
}
