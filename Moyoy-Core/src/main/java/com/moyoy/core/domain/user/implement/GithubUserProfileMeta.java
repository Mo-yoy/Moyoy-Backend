package com.moyoy.core.domain.user.implement;

import com.moyoy.infra.github.dto.GithubProfileResponse;

public record GithubUserProfileMeta(
	int followerCount,
	int followingCount
) {


	public static GithubUserProfileMeta from(GithubProfileResponse githubProfileResponse) {
		return new GithubUserProfileMeta(
			githubProfileResponse.followers(),
			githubProfileResponse.following()
		);
	}
}
