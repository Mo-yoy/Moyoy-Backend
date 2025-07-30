package com.moyo.backend.domain.batch.ranking.implement;

public record RankingCalculatorResult(
	long weekRankingPoint,
	long monthRankingPoint,
	long yearRankingPoint,
	String rankingGrade) {
}
