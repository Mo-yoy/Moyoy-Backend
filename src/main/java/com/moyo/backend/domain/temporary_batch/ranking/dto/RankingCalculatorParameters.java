package com.moyo.backend.domain.temporary_batch.ranking.dto;

public record RankingCalculatorParameters(
	int stars,
	int followers,
	CommitStatsSummary weekStats,
	CommitStatsSummary monthStats,
	CommitStatsSummary yearStats) {
	public record CommitStatsSummary(int commits, int commitLines) {
	}
}
