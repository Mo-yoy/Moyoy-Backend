package com.moyoy.api.pr_review.application.request;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;

import com.moyoy.infra.database.mysql.pr_review.request.PrReviewQueryConditionData;

public record SearchConditionData(
	Long userId,
	Status status,
	String order,
	Position position,
	Long lastReviewId,
	int size) {

	public static SearchConditionData of(Long userId, String status, String order, String position, Long lastReviewId, int size) {
		return new SearchConditionData(
			userId,
			Status.from(status),
			order,
			Position.from(position),
			lastReviewId,
			size);
	}

	public PrReviewQueryConditionData toQueryCondition() {
		return new PrReviewQueryConditionData(userId, status, order, position, lastReviewId, size);
	}
}
