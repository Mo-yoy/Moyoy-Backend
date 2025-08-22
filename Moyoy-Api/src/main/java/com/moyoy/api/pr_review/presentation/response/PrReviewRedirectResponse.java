package com.moyoy.api.pr_review.presentation.response;


import com.moyoy.api.pr_review.application.response.PrReviewCreateResult;
import com.moyoy.api.pr_review.application.response.PrReviewUpdateResult;

public record PrReviewRedirectResponse(
	Long prReviewId) {
	public static PrReviewRedirectResponse from(PrReviewCreateResult result) {
		return new PrReviewRedirectResponse(
			result.prReviewId());
	}

	public static PrReviewRedirectResponse from(PrReviewUpdateResult result) {
		return new PrReviewRedirectResponse(
			result.prReviewId());
	}
}
