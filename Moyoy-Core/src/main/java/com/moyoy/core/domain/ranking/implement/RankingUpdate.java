package com.moyoy.core.domain.ranking.implement;

public record RankingUpdate(
	String grade,
	long weeklyPoint,
	long monthlyPoint,
	long yearlyPoint
) {
}
