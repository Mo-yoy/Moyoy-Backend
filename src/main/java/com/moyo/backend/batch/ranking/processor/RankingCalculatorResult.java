package com.moyo.backend.batch.ranking.processor;

public record RankingCalculatorResult(
	long weekRankingPoint,
	long monthRankingPoint,
	long yearRankingPoint,
	String rankingGrade
) {

}
