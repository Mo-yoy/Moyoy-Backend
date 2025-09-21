package com.moyoy.api.pr_review.application.request;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;

public record SearchConditionData(
	Status status,
	String order,
	Position position,
	Long lastReviewId,
	int size) {

	public static SearchConditionData of(String status, String order, String position, Long lastReviewId, int size) {
		return new SearchConditionData(
				Status.from(status),
				order,
				Position.from(position),
				lastReviewId,
				size
		);
	}
}
