package com.moyoy.api.pr_review.presentation.response;

import com.moyoy.api.pr_review.application.response.PrReviewListResult;
import com.moyoy.api.pr_review.application.PrReviewSummary;

import java.util.List;

public record PrReviewListResponse(
	List<PrReviewSummary> prReviews,
	boolean isLast) {
	public static PrReviewListResponse from(PrReviewListResult result) {
		return new PrReviewListResponse(result.prReviewList(), result.isLast());
	}
}
