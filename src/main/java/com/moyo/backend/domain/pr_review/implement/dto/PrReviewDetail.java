package com.moyo.backend.domain.pr_review.implement.dto;

import com.moyo.backend.domain.pr_review.implement.PrReview;

public record PrReviewDetail(
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
	public static PrReviewDetail from(PrReview pr, Long userId) {
		return new PrReviewDetail(
			pr.isOpened() ? "open" : "closed",
			pr.getUser().getId().equals(userId),
			pr.isAdopted(),
			pr.getUser().getProfileImgUrl(),
			pr.getUser().getUsername(),
			pr.getPosition().toString(),
			pr.getTitle(),
			pr.getHitCount(),
			pr.getCreatedAt().toString(),
			pr.getContent(),
			pr.getPrUrl());
	}
}
