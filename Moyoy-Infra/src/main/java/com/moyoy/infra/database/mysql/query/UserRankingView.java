package com.moyoy.infra.database.mysql.query;

public record UserRankingView(
	Long userId,
	String username,
	long yearlyRankPoint,
	String yearlyRankGrade,
	String profileImgUrl) {
}
