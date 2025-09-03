package com.moyoy.api.user.application.request;

import static com.moyoy.common.constant.MoyoConstants.*;

import org.springframework.security.oauth2.core.user.OAuth2User;

public record UserSyncData(
	Integer githubUserId,
	String userTag,
	String profileImgUrl,
	String type,
	int followers,
	int following
) {

	public static UserSyncData from(OAuth2User oAuth2User) {
		return new UserSyncData(
			(Integer)oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_ID),
			oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_NAME).toString(),
			oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_AVATAR_URL).toString(),
			oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_TYPE).toString(),
			(Integer)oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_FOLLOWERS),
			(Integer)oAuth2User.getAttributes().get(GITHUB_OAUTH2_USER_FOLLOWING)
		);
	}
}
