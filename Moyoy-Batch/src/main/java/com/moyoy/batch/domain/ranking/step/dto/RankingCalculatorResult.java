package com.moyoy.batch.domain.ranking.step.dto;

public record RankingCalculatorResult(
	long weekRankingPoint,
	long monthRankingPoint,
	long yearRankingPoint,
	String rankingGrade) {
}
