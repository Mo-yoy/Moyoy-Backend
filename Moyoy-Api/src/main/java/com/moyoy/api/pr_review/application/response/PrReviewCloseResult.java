package com.moyoy.api.pr_review.application.response;

public record PrReviewCloseResult(
	Long prReviewId) {
	public static PrReviewCloseResult from(Long reviewId) {
		return new PrReviewCloseResult(reviewId);
	}
}
