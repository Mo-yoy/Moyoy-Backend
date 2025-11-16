package com.moyoy.api.pr_review.application.response;

import java.time.LocalDateTime;

import com.moyoy.infra.database.mysql.pr_review.response.PrReviewDetailData;

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

	public static PrReviewDetailResult from(PrReviewDetailData data, boolean isWriter) {
		return new PrReviewDetailResult(
			data.status().name(),
			isWriter,
			data.adopted(),
			data.profileImageUrl(),
			data.username(),
			data.position().name(),
			data.title(),
			data.hitCount(),
			data.createdAt(),
			data.closedAt(),
			data.content(),
			data.prUrl());
	}
}
