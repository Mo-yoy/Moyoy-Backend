package com.moyoy.domain.pr_review.dto;

import java.time.LocalDateTime;

import com.moyoy.domain.pr_review.Position;

public record PrReviewUpdate(
	String title,
	Position position,
	String prUrl,
	String content,
	LocalDateTime closedAt) {
}
