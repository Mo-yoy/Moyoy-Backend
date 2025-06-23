package com.moyo.backend.domain.pr_review.dto.response;

public record PrReviewUpdateFormResponseDto(
	String title,
	String position,
	String prUrl,
	String content) {
}
