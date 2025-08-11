package com.moyoy.batch.domain.ranking.step.dto;

public record RankingUpdateParameters(
	Long currentUserId,
	RankingCalculatorResult rankingCalculatorResult) {

	public static RankingUpdateParameters of(Long currentUserId, RankingCalculatorResult rankingCalculatorResult) {
		return new RankingUpdateParameters(currentUserId, rankingCalculatorResult);
	}
}
