package com.moyoy.domain.common.github;

import static com.moyoy.common.constant.MoyoConstants.*;

import org.springframework.security.oauth2.core.user.OAuth2User;

/// Github User Id는 기본으로 Integer로 제공
public record GithubUserProfileDto(
	Integer githubUserId,
	String username,
	String profileImgUrl) {

	public static GithubUserProfileDto from(OAuth2User oAuth2User) {
		return new GithubUserProfileDto(
			(Integer)oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_ID),
			oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_NAME).toString(),
			oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_AVATAR_URL).toString());
	}
}
