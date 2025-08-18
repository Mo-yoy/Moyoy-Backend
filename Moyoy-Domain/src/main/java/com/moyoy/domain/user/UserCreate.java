package com.moyoy.domain.user;

public record UserCreate(
	Integer githubUserId,
	String username,
	String profileImgUrl) {

	public static UserCreate of(Integer githubUserId, String username, String profileImgUrl) {
		return new UserCreate(githubUserId, username, profileImgUrl);
	}
}
