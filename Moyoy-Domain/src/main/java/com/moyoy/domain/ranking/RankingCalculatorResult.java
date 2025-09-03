package com.moyoy.domain.ranking;

public record RankingCalculatorResult(
	long weekRankingPoint,
	long monthRankingPoint,
	long yearRankingPoint,
	String rankingGrade) {
}
