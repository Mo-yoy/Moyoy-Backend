package com.moyoy.api.pr_review.application.request;

import java.time.LocalDateTime;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.dto.PrReviewUpdate;

public record PrReviewUpdateData(
	String title,
	Position position,
	String prUrl,
	String content,
	LocalDateTime closedAt) {

	public static PrReviewUpdateData of(String title, String position, String prUrl, String content, LocalDateTime closedAt) {
		return new PrReviewUpdateData(
			title,
			Position.from(position),
			prUrl,
			content,
			closedAt);
	}

	public PrReviewUpdate toCommand() {
		return new PrReviewUpdate(title, position, prUrl, content, closedAt);
	}
}
