package com.moyoy.batch.legacy.domain.ranking.step.dto;

import com.moyoy.domain.ranking.RankingCalculatorResult;

public record RankingUpdateParameters(
	Long currentUserId,
	RankingCalculatorResult rankingCalculatorResult) {

	public static RankingUpdateParameters of(Long currentUserId, RankingCalculatorResult rankingCalculatorResult) {
		return new RankingUpdateParameters(currentUserId, rankingCalculatorResult);
	}
}
