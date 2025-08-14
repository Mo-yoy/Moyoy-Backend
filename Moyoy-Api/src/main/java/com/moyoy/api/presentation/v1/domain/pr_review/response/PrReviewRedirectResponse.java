package com.moyoy.api.presentation.v1.domain.pr_review.response;

import com.moyoy.core.domain.pr_review.business.dto.PrReviewCreateResult;
import com.moyoy.core.domain.pr_review.business.dto.PrReviewUpdateResult;

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
