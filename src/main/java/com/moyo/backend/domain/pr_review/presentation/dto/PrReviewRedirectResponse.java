package com.moyo.backend.domain.pr_review.presentation.dto;

import com.moyo.backend.domain.pr_review.business.dto.PrReviewCreateResult;
import com.moyo.backend.domain.pr_review.business.dto.PrReviewUpdateResult;

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
