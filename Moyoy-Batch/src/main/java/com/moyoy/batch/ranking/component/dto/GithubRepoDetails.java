package com.moyoy.batch.ranking.component.dto;

import com.moyoy.infra.github.dto.GithubRepoResponse;

public record GithubRepoDetails(
	String repoName,
	String ownerName,
	int starCount) {

	public static GithubRepoDetails from(GithubRepoResponse response) {
		return new GithubRepoDetails(response.fullName(), response.owner().login(), response.stargazersCount());
	}
}
