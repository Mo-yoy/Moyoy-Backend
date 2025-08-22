package com.moyoy.api.pr_review.presentation.response;

import com.moyoy.api.pr_review.application.response.PrReviewContentResult;

public record PrReviewUpdateFormResponse(
	String title,
	String position,
	String prUrl,
	String content) {

	public static PrReviewUpdateFormResponse from(PrReviewContentResult result) {
		return new PrReviewUpdateFormResponse(
			result.title(),
			result.position(),
			result.prUrl(),
			result.content());
	}
}
