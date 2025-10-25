package com.moyoy.api.pr_review.application.response;

import java.util.List;

import com.moyoy.api.pr_review.application.PrReviewSummary;

import com.moyoy.infra.database.mysql.pr_review.response.PrReviewSummaryData;

import com.moyoy.common.page.SliceResult;

public record PrReviewListResult(
	List<PrReviewSummary> prReviewList,
	boolean isLast) {

	public static PrReviewListResult from(SliceResult<PrReviewSummaryData> slice) {
		List<PrReviewSummary> summaries = slice.content().stream()
			.map(PrReviewSummary::from)
			.toList();

		return new PrReviewListResult(summaries, slice.isLast());
	}
}
