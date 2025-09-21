package com.moyoy.api.pr_review.presentation.request;

import com.moyoy.api.pr_review.application.request.SearchConditionData;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PrReviewListRequest(
	@NotBlank @Pattern(regexp = "open|closed")String status,
	String order,
	String position,
	@Min(0) Long lastReviewId,
	@Min(1) @Max(100) Integer size) {

	public PrReviewListRequest {
		if (order == null)
			order = "createdAt-desc";

		if (lastReviewId == null)
			lastReviewId = 0L;

		if (size == null)
			size = 20;
	}

	public SearchConditionData toSearchCondition() {
		return SearchConditionData.of(status, order, position, lastReviewId, size);
	}
}
