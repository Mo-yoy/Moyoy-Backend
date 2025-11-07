package com.moyoy.api.pr_review.application.request;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;

import com.moyoy.domain.pr_review.error.InvalidOrderException;
import com.moyoy.infra.database.mysql.pr_review.request.PrReviewQueryConditionData;

import java.util.List;

public record SearchConditionData(
	Long userId,
	Status status,
	String order,
	Position position,
	Long lastReviewId,
	int size) {

	public static SearchConditionData of(Long userId, String status, String order, String position, Long lastReviewId, int size) {

		validateOrder(order);

		return new SearchConditionData(
			userId,
			Status.from(status),
			order,
			Position.from(position),
			lastReviewId,
			size);
	}

	private static void validateOrder(String order) {
		if (order == null || order.isBlank()) return;

		List<String> validFields = List.of("createdAt", "hitCount");
		List<String> validDirections = List.of("asc", "desc");

		for (String token : order.split(",")) {
			String[] parts = token.split("-");
			if (parts.length != 2
				|| !validFields.contains(parts[0])
				|| !validDirections.contains(parts[1].toLowerCase())) {
				throw new InvalidOrderException();
			}
		}
	}

	public PrReviewQueryConditionData toQueryCondition() {
		return new PrReviewQueryConditionData(userId, status, order, position, lastReviewId, size);
	}
}
