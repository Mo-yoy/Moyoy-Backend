package com.moyoy.infra.database.mysql.pr_review.response;

import java.time.LocalDateTime;

import com.moyoy.domain.pr_review.Position;

public record PrReviewSummaryData(
	String profileImageUrl,
	String username,
	Position position,
	String title,
	int hitCount,
	LocalDateTime createdAt) {
}
