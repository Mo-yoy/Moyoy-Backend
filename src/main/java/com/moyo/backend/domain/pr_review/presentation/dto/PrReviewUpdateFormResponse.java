package com.moyo.backend.domain.pr_review.presentation.dto;

import com.moyo.backend.domain.pr_review.business.dto.PrReviewContent;

public record PrReviewUpdateFormResponse(
	String title,
	String position,
	String prUrl,
	String content) {

	public static PrReviewUpdateFormResponse from(PrReviewContent result) {
		return new PrReviewUpdateFormResponse(
			result.title(),
			result.position(),
			result.prUrl(),
			result.content());
	}
}
