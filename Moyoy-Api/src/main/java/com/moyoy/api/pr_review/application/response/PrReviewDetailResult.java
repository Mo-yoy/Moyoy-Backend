package com.moyoy.api.pr_review.application.response;

import com.moyoy.domain.pr_review.PrReview;

import java.time.LocalDateTime;

public record PrReviewDetailResult(
	String status,
	boolean isWriter,
	boolean isAdopted,
	String profileImageUrl,
	String username,
	String position,
	String title,
	int hitCount,
	LocalDateTime createdAt,
	LocalDateTime closedAt,
	String content,
	String prUrl) {
	public static PrReviewDetailResult from(PrReview prReview, boolean isWriter) {
		return new PrReviewDetailResult(
			prReview.getStatus().getValue(),
			isWriter,
			prReview.isAdopted(),
			prReview.getAuthor().profileImgUrl(),
			prReview.getAuthor().username(),
			prReview.getPosition().getValue(),
			prReview.getTitle(),
			prReview.getHitCount(),
			prReview.getCreatedAt(),
			prReview.getClosedAt(),
			prReview.getContent(),
			prReview.getPrUrl());
	}
}
