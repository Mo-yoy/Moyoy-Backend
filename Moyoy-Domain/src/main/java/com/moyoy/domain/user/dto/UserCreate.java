package com.moyoy.domain.user.dto;

import com.moyoy.domain.user.SocialSize;

public record UserCreate(
	Integer githubUserId,
	String username,
	String profileImgUrl,
	SocialSize socialSize) {

	public static UserCreate of(Integer githubUserId, String username, String profileImgUrl, SocialSize socialSize) {
		return new UserCreate(githubUserId, username, profileImgUrl, socialSize);
	}
}
