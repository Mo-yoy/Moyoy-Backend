package com.moyoy.api.pr_review.application.request;


import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.PrReviewCreate;

import java.time.LocalDateTime;

public record PrReviewContentData(
	String title,
	Position position,
	String prUrl,
	String content,
	LocalDateTime closedAt) {

	public static PrReviewContentData of(String title, String position, String prUrl, String content, LocalDateTime closedAt) {
		return new PrReviewContentData(
			title,
			Position.from(position),
			prUrl,
			content,
			closedAt
		);
	}

//	public PrReviewCreate toCreateContent() {
//		return new PrReviewCreate(title, position, prUrl, content);
//	}
}
