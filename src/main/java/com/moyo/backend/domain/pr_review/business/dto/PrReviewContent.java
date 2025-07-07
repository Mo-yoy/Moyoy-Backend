package com.moyo.backend.domain.pr_review.business.dto;

import com.moyo.backend.domain.pr_review.implement.dto.PrReviewContentData;

public record PrReviewContent(
	String title,
	String position,
	String prUrl,
	String content) {

	public static PrReviewContent from(PrReviewContentData data) {
		return new PrReviewContent(
			data.title(),
			data.position(),
			data.prUrl(),
			data.content());
	}
}
