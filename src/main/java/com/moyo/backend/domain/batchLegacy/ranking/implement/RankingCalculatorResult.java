package com.moyo.backend.domain.batchLegacy.ranking.implement;

public record RankingCalculatorResult(
	long weekRankingPoint,
	long monthRankingPoint,
	long yearRankingPoint,
	String rankingGrade) {
}
