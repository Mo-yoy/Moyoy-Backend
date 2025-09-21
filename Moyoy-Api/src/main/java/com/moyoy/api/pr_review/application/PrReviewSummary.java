package com.moyoy.api.pr_review.application;

import com.moyoy.domain.pr_review.PrReview;

import java.time.LocalDateTime;

public record PrReviewSummary(
		String profileImageUrl,
		String username,
		String position,
		String title,
		LocalDateTime createdAt,
		int hitCount) {
	public static PrReviewSummary from(PrReview pr) {
		return new PrReviewSummary(
			pr.getAuthor().profileImgUrl(),
			pr.getAuthor().username(),
			pr.getPosition().toString(),
			pr.getTitle(),
			pr.getCreatedAt(),
			pr.getHitCount());
	}
}
