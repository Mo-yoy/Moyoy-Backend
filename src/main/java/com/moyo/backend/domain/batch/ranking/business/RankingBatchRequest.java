package com.moyo.backend.domain.batch.ranking.business;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistory;

public record RankingBatchRequest(
	Long lastUserId,
	RankingBatchHistory rankingBatchHistory) {
}
