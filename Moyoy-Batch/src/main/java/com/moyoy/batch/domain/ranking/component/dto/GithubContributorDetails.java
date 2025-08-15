package com.moyoy.batch.domain.ranking.component.dto;

import com.moyoy.infra.external.github.dto.GithubRepoContributorsResponse;

public record GithubContributorDetails(
	String username) {
	public static GithubContributorDetails from(GithubRepoContributorsResponse githubRepoContributorsResponse) {
		return new GithubContributorDetails(githubRepoContributorsResponse.login());
	}
}
