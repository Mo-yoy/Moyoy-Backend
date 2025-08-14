package com.moyoy.api.presentation.v1.domain.pr_review.response;

import com.moyoy.core.domain.pr_review.business.dto.PrReviewContent;

public record PrReviewUpdateFormResponse(
	String title,
	String position,
	String prUrl,
	String content) {

	public static PrReviewUpdateFormResponse from(PrReviewContent result) {
		return new PrReviewUpdateFormResponse(
			result.title(),
			result.position(),
			result.prUrl(),
			result.content());
	}
}
