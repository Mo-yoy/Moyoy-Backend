package com.moyoy.domain.ranking.dto;

public record RankingUpdate(
	String grade,
	long weeklyPoint,
	long monthlyPoint,
	long yearlyPoint) {
}
