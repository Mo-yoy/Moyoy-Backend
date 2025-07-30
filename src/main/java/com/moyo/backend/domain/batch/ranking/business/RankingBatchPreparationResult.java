package com.moyo.backend.domain.batch.ranking.business;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistory;
import com.moyo.backend.domain.batch.ranking.dto.UserRankingBatchSnapshot;

public record RankingBatchPreparationResult(
	UserRankingBatchSnapshot userRankingBatchSnapshot,
	RankingBatchHistory rankingBatchHistory) {
}
