package com.moyoy.core.domain.pr_review.implement.dto;

import com.moyoy.core.domain.pr_review.implement.PrReview;

public record PrReviewContentData(
	String title,
	String position,
	String prUrl,
	String content,
	boolean isWriter) {
	public static PrReviewContentData from(PrReview prReview, Long userId) {
		return new PrReviewContentData(
			prReview.getTitle(),
			prReview.getPosition().toString(),
			prReview.getPrUrl(),
			prReview.getContent(),
			prReview.getUser().getId().equals(userId));
	}
}
