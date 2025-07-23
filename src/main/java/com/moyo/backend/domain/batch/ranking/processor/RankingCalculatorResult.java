package com.moyo.backend.domain.batch.ranking.processor;

public record RankingCalculatorResult(
	long weekRankingPoint,
	long monthRankingPoint,
	long yearRankingPoint,
	String rankingGrade) {

}
