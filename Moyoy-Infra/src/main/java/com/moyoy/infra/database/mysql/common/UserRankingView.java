package com.moyoy.infra.database.mysql.common;

public record UserRankingView(
	Long userId,
	String username,
	long yearlyRankPoint,
	String yearlyRankGrade,
	String profileImgUrl) {
}
