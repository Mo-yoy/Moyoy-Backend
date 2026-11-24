package com.moyoy.domain.pr_review.dto;

public record PrReviewHitCreate(
	Long prReviewId,
	Long userId,
	String clientIp) {
}
