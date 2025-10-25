package com.moyoy.infra.database.mysql.pr_review;

import java.util.Optional;

import com.moyoy.infra.database.mysql.pr_review.request.PrReviewQueryConditionData;
import com.moyoy.infra.database.mysql.pr_review.response.PrReviewDetailData;
import com.moyoy.infra.database.mysql.pr_review.response.PrReviewSummaryData;

import com.moyoy.common.page.SliceResult;

public interface PrReviewQueryRepository {
	SliceResult<PrReviewSummaryData> findAll(PrReviewQueryConditionData condition);

	Optional<PrReviewDetailData> findById(Long reviewId);
}
