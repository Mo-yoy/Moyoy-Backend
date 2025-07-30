package com.moyo.backend.domain.batch.ranking.business;

public record RankingBatchRequest(
	Long lastUserId,
	Long rankingBatchHistoryId
) {
}
