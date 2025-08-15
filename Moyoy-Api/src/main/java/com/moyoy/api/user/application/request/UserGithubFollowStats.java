package com.moyoy.api.user.application.request;

import com.moyoy.infra.external.github.dto.GithubProfileResponse;

public record UserGithubFollowStats(
	int followerCount,
	int followingCount) {

	public static UserGithubFollowStats from(GithubProfileResponse githubProfileResponse) {
		return new UserGithubFollowStats(
			githubProfileResponse.followers(),
			githubProfileResponse.following());
	}
}
