package com.moyoy.infra.database.mysql.pr_review.response;

import java.time.LocalDateTime;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;

public record PrReviewSummaryData(
	String profileImageUrl,
	String username,
	Status status,
	Position position,
	String title,
	int hitCount,
	LocalDateTime createdAt) {
}
