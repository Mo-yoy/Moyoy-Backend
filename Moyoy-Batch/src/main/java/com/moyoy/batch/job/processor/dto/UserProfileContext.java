package com.moyoy.batch.job.processor.dto;

public record UserProfileContext(
	UserAuthContext auth,
	String username,
	int followerCount) {
}
