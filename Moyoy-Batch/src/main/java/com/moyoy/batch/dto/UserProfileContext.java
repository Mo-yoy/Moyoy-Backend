package com.moyoy.batch.dto;

public record UserProfileContext(
	UserAuthContext auth,
	String username,
	int followerCount
) {}

