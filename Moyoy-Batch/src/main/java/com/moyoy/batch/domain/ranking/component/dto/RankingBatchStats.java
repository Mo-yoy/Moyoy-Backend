package com.moyoy.batch.domain.ranking.component.dto;

public record RankingBatchStats(
	int successCount,
	int failCount) {

	public static RankingBatchStats of(int successCount, int failCount) {
		return new RankingBatchStats(successCount, failCount);
	}
}
