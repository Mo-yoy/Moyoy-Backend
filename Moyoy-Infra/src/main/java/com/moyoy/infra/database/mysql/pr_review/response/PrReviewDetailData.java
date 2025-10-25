package com.moyoy.infra.database.mysql.pr_review.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.Status;

public record PrReviewDetailData(
	Status status,
	Long userId,
	String username,
	String profileImageUrl,
	String title,
	Position position,
	String prUrl,
	String content,
	int hitCount,
	boolean adopted,
	LocalDateTime createdAt,
	LocalDateTime closedAt) {
	@QueryProjection
	public PrReviewDetailData {
	}
}
