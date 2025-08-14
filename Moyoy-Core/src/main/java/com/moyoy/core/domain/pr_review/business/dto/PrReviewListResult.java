package com.moyoy.core.domain.pr_review.business.dto;

import java.util.List;

import com.moyoy.core.domain.pr_review.implement.dto.PrReviewSummary;

public record PrReviewListResult(
	List<PrReviewSummary> prReviewList,
	boolean isLast) {
}
