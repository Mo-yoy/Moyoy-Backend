package com.moyoy.core.domain.pr_review.implement.dto;

import java.util.List;

public record PrReviewListData(
	List<PrReviewSummary> prReviews,
	boolean isLast) {
}
