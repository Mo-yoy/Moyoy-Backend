package com.moyo.backend.domain.auth.oauth.dto;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import org.springframework.security.oauth2.core.user.OAuth2User;

/// Github User Id는 기본으로 Integer로 제공
public record GithubUserDto(
	Integer githubUserId,
	String username,
	String profileImgUrl,
	String userTag
) {

	public static GithubUserDto from(OAuth2User oAuth2User) {
		return new GithubUserDto(
			(Integer)oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_ID),
			oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_NAME).toString(),
			oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_AVATAR_URL).toString(),
			oAuth2User.getAttributes().get("name").toString() // 임시 테스트 용
		);
	}
}
