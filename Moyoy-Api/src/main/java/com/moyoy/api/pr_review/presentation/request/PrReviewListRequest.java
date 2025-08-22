package com.moyoy.api.pr_review.presentation.request;

import com.moyoy.api.pr_review.application.request.SearchCondition;
import jakarta.validation.constraints.NotBlank;

public record PrReviewListRequest(
	@NotBlank String status,
	String order,
	String position,
	Integer page,
	Integer size) {
	public PrReviewListRequest {
		status = (status.equals("open")) ? "open" : "closed";
		order = (order == null) ? "createdAt-desc" : order;
		page = (page == null) ? 0 : page;
		size = (size == null) ? 10 : size;
	}

	public SearchCondition toSearchCondition() {
		return SearchCondition.of(status, order, position, page, size);
	}
}
