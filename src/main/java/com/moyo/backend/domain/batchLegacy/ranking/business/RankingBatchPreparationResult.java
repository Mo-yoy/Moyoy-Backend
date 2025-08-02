package com.moyo.backend.domain.batchLegacy.ranking.business;

import com.moyo.backend.domain.batchLegacy.ranking.implement.UserRankingBatchSnapshot;
import com.moyo.backend.domain.batchLegacy.ranking.implement.RankingBatchHistory;

public record RankingBatchPreparationResult(
	UserRankingBatchSnapshot userRankingBatchSnapshot,
	RankingBatchHistory rankingBatchHistory) {
}
