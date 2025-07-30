package com.moyo.backend.domain.batch.ranking.implement;

public record RankingBatchResult(
	int successCount,
	int failCount
) {

	public static RankingBatchResult of(int successCount, int failCount) {
		return new RankingBatchResult(successCount, failCount);
	}
}
