package com.moyoy.infra.database.mysql.query.dto;

public record UserRankingView(
	Long userId,
	String username,
	long yearlyRankPoint,
	String yearlyRankGrade,
	String profileImgUrl) {
}
