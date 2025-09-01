package com.moyoy.batch.legacy.domain.ranking.component.dto;

public record RankingBatchStats(
	int successCount,
	int failCount) {

	public static RankingBatchStats of(int successCount, int failCount) {
		return new RankingBatchStats(successCount, failCount);
	}
}
