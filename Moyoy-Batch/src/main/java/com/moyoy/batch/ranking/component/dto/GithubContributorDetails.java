package com.moyoy.batch.ranking.component.dto;

import com.moyoy.infra.github.dto.GithubRepoContributorsResponse;

public record GithubContributorDetails(
	String username) {
	public static GithubContributorDetails from(GithubRepoContributorsResponse githubRepoContributorsResponse) {
		return new GithubContributorDetails(githubRepoContributorsResponse.login());
	}
}
