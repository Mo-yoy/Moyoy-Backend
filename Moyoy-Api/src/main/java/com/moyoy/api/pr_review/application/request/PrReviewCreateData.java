package com.moyoy.api.pr_review.application.request;

import java.time.LocalDateTime;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.dto.PrReviewCreate;

public record PrReviewCreateData(
	String title,
	Position position,
	String prUrl,
	String content,
	LocalDateTime closedAt) {

	public static PrReviewCreateData of(String title, String position, String prUrl, String content, LocalDateTime closedAt) {
		return new PrReviewCreateData(
			title,
			Position.from(position),
			prUrl,
			content,
			closedAt);
	}

	public PrReviewCreate toCommand(Long userId) {
		return new PrReviewCreate(userId, title, position, prUrl, content, closedAt);
	}
}
