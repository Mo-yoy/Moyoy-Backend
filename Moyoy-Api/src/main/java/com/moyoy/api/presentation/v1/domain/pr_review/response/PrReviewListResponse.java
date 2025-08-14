package com.moyoy.api.presentation.v1.domain.pr_review.response;

import java.util.List;

import com.moyoy.core.domain.pr_review.business.dto.PrReviewListResult;
import com.moyoy.core.domain.pr_review.implement.dto.PrReviewSummary;

public record PrReviewListResponse(
	List<PrReviewSummary> prReviews,
	boolean isLast) {
	public static PrReviewListResponse from(PrReviewListResult result) {
		return new PrReviewListResponse(result.prReviewList(), result.isLast());
	}
}
