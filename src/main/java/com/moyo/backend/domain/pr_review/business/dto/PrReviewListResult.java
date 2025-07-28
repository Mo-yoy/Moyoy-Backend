package com.moyo.backend.domain.pr_review.business.dto;

import java.util.List;

import com.moyo.backend.domain.pr_review.implement.dto.PrReviewSummary;

public record PrReviewListResult(
	List<PrReviewSummary> prReviewList,
	boolean isLast) {
}
