package com.moyo.backend.domain.batchLegacy.ranking.implement;

import com.moyo.backend.domain.batchLegacy.ranking.business.RankingBatchPreparationResult;

public record RankingBatchRequest(
	Long lastUserId,
	RankingBatchHistory rankingBatchHistory) {

	public static RankingBatchRequest from(RankingBatchPreparationResult preparationResult) {
		return new RankingBatchRequest(preparationResult.userRankingBatchSnapshot().lastUserId(), preparationResult.rankingBatchHistory());
	}
}
