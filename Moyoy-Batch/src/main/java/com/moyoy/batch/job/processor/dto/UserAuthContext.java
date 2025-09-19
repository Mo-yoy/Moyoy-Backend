package com.moyoy.batch.job.processor.dto;

public record UserAuthContext(
	Long userId,
	Integer githubUserId,
	String githubAccessToken) {
}
