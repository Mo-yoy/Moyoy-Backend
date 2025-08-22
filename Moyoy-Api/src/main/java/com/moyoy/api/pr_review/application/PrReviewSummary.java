package com.moyoy.api.pr_review.application;

import com.moyoy.domain.pr_review.PrReview;

import static com.moyoy.common.util.TimeSinceFormatter.*;

public record PrReviewSummary(
	String profileImageUrl,
	String username,
	String position,
	String title,
	String since,
	int hitCount) {
	public static PrReviewSummary from(PrReview pr) {
		return new PrReviewSummary(
			pr.getAuthor().profileImgUrl(),
			pr.getAuthor().username(),
			pr.getPosition().toString(),
			pr.getTitle(),
			formatTimeSince(pr.getCreatedAt()),
			pr.getHitCount());
	}
}
