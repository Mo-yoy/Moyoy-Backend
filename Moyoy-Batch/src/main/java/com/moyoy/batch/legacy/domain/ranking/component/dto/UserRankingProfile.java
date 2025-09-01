package com.moyoy.batch.legacy.domain.ranking.component.dto;

import com.moyoy.infra.external.github.user.GithubUserResponse;

public record UserRankingProfile(
	String username,
	int followerCount) {

	public static UserRankingProfile from(GithubUserResponse githubUserResponse) {
		return new UserRankingProfile(githubUserResponse.login(), githubUserResponse.followers());
	}
}
