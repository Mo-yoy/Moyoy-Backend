package com.moyoy.api.pr_review.presentation.response;

import java.util.List;

import com.moyoy.api.pr_review.application.PrReviewSummary;
import com.moyoy.api.pr_review.application.response.PrReviewListResult;

public record PrReviewListResponse(
	List<PrReviewSummary> prReviews,
	boolean isLast) {
	public static PrReviewListResponse from(PrReviewListResult result) {
		return new PrReviewListResponse(result.prReviewList(), result.isLast());
	}
}
