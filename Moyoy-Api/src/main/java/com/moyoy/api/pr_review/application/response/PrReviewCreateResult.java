package com.moyoy.api.pr_review.application.response;

public record PrReviewCreateResult(
	Long prReviewId) {
	public static PrReviewCreateResult from(Long reviewId){
		return new PrReviewCreateResult(reviewId);
	}
}
