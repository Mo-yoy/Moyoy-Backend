package com.moyo.backend.domain.pr_review.presentation.dto;

import java.util.List;

import com.moyo.backend.domain.pr_review.business.dto.PrReviewListResult;
import com.moyo.backend.domain.pr_review.implement.dto.PrReviewSummary;

public record PrReviewListResponse(
	List<PrReviewSummary> prReviews,
	boolean isLast) {
	public static PrReviewListResponse from(PrReviewListResult result) {
		return new PrReviewListResponse(result.prReviewList(), result.isLast());
	}
}
