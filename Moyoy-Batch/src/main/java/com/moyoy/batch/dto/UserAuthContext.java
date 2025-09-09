package com.moyoy.batch.dto;

public record UserAuthContext(
	Long userId,
	Integer githubUserId,
	String githubAccessToken) {
}
