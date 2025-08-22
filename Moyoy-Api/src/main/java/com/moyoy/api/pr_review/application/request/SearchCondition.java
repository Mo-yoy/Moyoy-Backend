package com.moyoy.api.pr_review.application.request;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;

public record SearchCondition(
	Status status,
	String order,
	Position position,
	int page,
	int size) {

	public static SearchCondition of(String status, String order, String position, int page, int size) {
		return new SearchCondition(
				Status.from(status),
				order,
				Position.from(position),
				page,
				size
		);
	}
//	public static SearchCondition create(PrReviewListRequest request) {
//		return new SearchCondition(
//			Status.create(request.status()),
//			request.order(),
//			Position.create(request.position()),
//			request.page(),
//			request.size());
//	}
}
