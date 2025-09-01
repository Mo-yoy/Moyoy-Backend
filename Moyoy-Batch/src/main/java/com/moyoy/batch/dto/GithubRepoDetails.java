package com.moyoy.batch.dto;

import com.moyoy.infra.external.github.repo.GithubRepoResponse;

public record GithubRepoDetails(
	String repoFullName,
	String ownerName,
	int starCount) {

	public static GithubRepoDetails from(GithubRepoResponse response) {
		return new GithubRepoDetails(response.fullName(), response.owner().login(), response.stargazersCount());
	}
}
