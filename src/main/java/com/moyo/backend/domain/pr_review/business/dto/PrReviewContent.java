package com.moyo.backend.domain.pr_review.business.dto;

public record PrReviewContent(
	String title,
	String position,
	String prUrl,
	String content) {
}
