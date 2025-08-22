package com.moyoy.api.pr_review.presentation.response;


import com.moyoy.api.pr_review.application.response.PrReviewDetailResult;

import java.time.LocalDateTime;

public record PrReviewDetailResponse(
	String status,
	boolean isWriter,
	boolean isAdopted,
	String profileImageUrl,
	String username,
	String position,
	String title,
	int hitCount,
	LocalDateTime createdAt,
	String content,
	String prUrl) {

	public static PrReviewDetailResponse from(PrReviewDetailResult result) {
		return new PrReviewDetailResponse(
			result.status(),
			result.isWriter(),
			result.isAdopted(),
			result.profileImageUrl(),
			result.username(),
			result.position(),
			result.title(),
			result.hitCount(),
			result.createdAt(),
			result.content(),
			result.prUrl());
	}
}
