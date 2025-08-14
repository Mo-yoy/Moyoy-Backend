package com.moyoy.api.presentation.v1.domain.pr_review.response;

import com.moyoy.core.domain.pr_review.business.dto.PrReviewDetailResult;

public record PrReviewDetailResponse(
	String status,
	boolean isWriter,
	boolean isAdopted, // 채택은 스프린트2에서 추가 TODO
	String profileImageUrl,
	String username,
	String position,
	String title,
	int hitCount,
	String createdAt,
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
