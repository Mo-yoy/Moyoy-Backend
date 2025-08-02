package com.moyo.backend.domain.pr_review.implement.dto;

import static com.moyo.backend.common.util.TimeSinceFormatter.*;

import com.moyo.backend.domain.pr_review.implement.PrReview;

public record PrReviewSummary(
	String profileImageUrl,
	String username,
	String position,
	String title,
	String since,
	int hitCount) {
	public static PrReviewSummary from(PrReview pr) {
		return new PrReviewSummary(
			pr.getUser().getProfileImgUrl(),
			pr.getUser().getUsername(),
			pr.getPosition().toString(),
			pr.getTitle(),
			formatTimeSince(pr.getCreatedAt()),
			pr.getHitCount());
	}
}
