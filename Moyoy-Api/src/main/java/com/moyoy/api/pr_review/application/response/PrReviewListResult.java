package com.moyoy.api.pr_review.application.response;

import com.moyoy.api.pr_review.application.PrReviewSummary;
import com.moyoy.domain.pr_review.PrReview;
import com.moyoy.common.page.SliceResult;

import java.util.List;

public record PrReviewListResult(
	List<PrReviewSummary> prReviewList,
	boolean isLast) {

	public static PrReviewListResult from(SliceResult<PrReview> result) {

		List<PrReviewSummary> list = result.content().stream()
				.map(PrReviewSummary::from)
				.toList();

		return new PrReviewListResult(list, result.isLast());
	}
}
