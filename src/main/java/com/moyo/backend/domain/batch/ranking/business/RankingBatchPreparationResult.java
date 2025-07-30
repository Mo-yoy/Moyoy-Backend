package com.moyo.backend.domain.batch.ranking.business;

import com.moyo.backend.domain.batch.ranking.dto.UserRankingBatchSnapshot;
import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistory;

public record RankingBatchPreparationResult(
	UserRankingBatchSnapshot userRankingBatchSnapshot,
	RankingBatchHistory rankingBatchHistory) {
}
