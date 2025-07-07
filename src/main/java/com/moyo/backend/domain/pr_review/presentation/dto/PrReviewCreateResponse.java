package com.moyo.backend.domain.pr_review.presentation.dto;

import com.moyo.backend.domain.pr_review.business.dto.PrReviewCreateResult;

public record PrReviewCreateResponse(
	Long prReviewId) {
	public static PrReviewCreateResponse from(PrReviewCreateResult result) {
		return new PrReviewCreateResponse(
			result.prReviewId());
	}
}
