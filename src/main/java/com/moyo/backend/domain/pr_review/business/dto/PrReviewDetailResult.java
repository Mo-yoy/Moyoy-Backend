package com.moyo.backend.domain.pr_review.business.dto;

import com.moyo.backend.domain.pr_review.implement.dto.PrReviewDetail;

public record PrReviewDetailResult(
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
	public static PrReviewDetailResult from(PrReviewDetail pr) {
		return new PrReviewDetailResult(
			pr.status(),
			pr.isWriter(),
			pr.isAdopted(),
			pr.profileImageUrl(),
			pr.username(),
			pr.position(),
			pr.title(),
			pr.hitCount(),
			pr.createdAt(),
			pr.content(),
			pr.prUrl());
	}
}
