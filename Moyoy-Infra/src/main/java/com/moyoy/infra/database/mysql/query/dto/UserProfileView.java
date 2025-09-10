package com.moyoy.infra.database.mysql.query.dto;

public record UserProfileView(
	Long userId,
	String username,
	long yearlyRankPoint,
	String yearlyRankGrade,
	String profileImgUrl) {
}
