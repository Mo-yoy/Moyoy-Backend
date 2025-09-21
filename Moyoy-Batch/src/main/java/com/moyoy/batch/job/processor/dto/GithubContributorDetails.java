package com.moyoy.batch.job.processor.dto;

import com.moyoy.infra.external.github.repo.dto.GithubRepoContributorsResponse;

public record GithubContributorDetails(
	String username) {
	public static GithubContributorDetails from(GithubRepoContributorsResponse githubRepoContributorsResponse) {
		return new GithubContributorDetails(githubRepoContributorsResponse.login());
	}
}
