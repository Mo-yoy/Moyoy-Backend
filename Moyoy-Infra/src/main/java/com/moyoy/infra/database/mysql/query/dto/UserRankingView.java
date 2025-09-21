package com.moyoy.infra.database.mysql.query.dto;

public record UserRankingView(
	Long userId,
	String profileImageUrl,
	String username,
	long rankPoint) {
}
