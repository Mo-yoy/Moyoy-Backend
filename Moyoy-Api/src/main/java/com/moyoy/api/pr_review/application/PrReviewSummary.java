package com.moyoy.api.pr_review.application;

import java.time.LocalDateTime;

import com.moyoy.infra.database.mysql.pr_review.response.PrReviewSummaryData;

public record PrReviewSummary(
	String profileImageUrl,
	String username,
	String position,
	String title,
	int hitCount,
	LocalDateTime createdAt) {
	public static PrReviewSummary from(PrReviewSummaryData data) {
		return new PrReviewSummary(
			data.profileImageUrl(),
			data.username(),
			data.position().toString(),
			data.title(),
			data.hitCount(),
			data.createdAt());
	}
}
