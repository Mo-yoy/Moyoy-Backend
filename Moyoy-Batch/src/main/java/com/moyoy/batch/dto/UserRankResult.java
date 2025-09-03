package com.moyoy.batch.dto;

import com.moyoy.domain.ranking.RankingCalculatorResult;

public record UserRankResult(
	Long userId,
	RankingCalculatorResult rankingCalculatorResult
) {
}
