package com.moyoy.batch.dto;

import com.moyoy.domain.ranking.dto.RankingCalculatorResult;

public record UserRankResult(
	Long userId,
	RankingCalculatorResult rankingCalculatorResult
) {
}
