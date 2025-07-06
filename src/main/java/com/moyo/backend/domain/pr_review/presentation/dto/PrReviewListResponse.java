package com.moyo.backend.domain.pr_review.presentation.dto;

import java.util.List;

import com.moyo.backend.domain.pr_review.business.dto.PrReviewListResult;
import com.moyo.backend.domain.pr_review.implement.dto.PrReviewSummary;

public record PrReviewListResponse(
	List<PrReviewSummary> prReviews,
	boolean isLast) {

	public static PrReviewListResponse from(PrReviewListResult result) {
		List<PrReviewSummary> summaries = result.prReviewList().stream()
			.map(PrReviewSummary::from)
			.toList();
		return new PrReviewListResponse(summaries, result.isLast());
	}
}
