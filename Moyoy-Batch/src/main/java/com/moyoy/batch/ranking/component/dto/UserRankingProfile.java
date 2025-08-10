package com.moyoy.batch.ranking.component.dto;

import com.moyoy.infra.github.dto.GithubProfileResponse;

public record UserRankingProfile(
	String username,
	int followerCount) {

	public static UserRankingProfile from(GithubProfileResponse githubProfileResponse) {
		return new UserRankingProfile(githubProfileResponse.login(), githubProfileResponse.followers());
	}
}
