package com.moyoy.domain.ranking;

public record RankingUpdate(
	String grade,
	long weeklyPoint,
	long monthlyPoint,
	long yearlyPoint) {
}
