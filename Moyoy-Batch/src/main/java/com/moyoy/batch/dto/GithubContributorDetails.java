package com.moyoy.batch.dto;

import com.moyoy.infra.external.github.repo.GithubRepoContributorsResponse;

public record GithubContributorDetails(
	String username) {
	public static GithubContributorDetails from(GithubRepoContributorsResponse githubRepoContributorsResponse) {
		return new GithubContributorDetails(githubRepoContributorsResponse.login());
	}
}
