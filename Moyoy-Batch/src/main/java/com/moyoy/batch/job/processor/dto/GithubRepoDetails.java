package com.moyoy.batch.job.processor.dto;

import com.moyoy.infra.external.github.repo.dto.GithubRepoResponse;

public record GithubRepoDetails(
	String repoFullName,
	String ownerName,
	int starCount) {

	public static GithubRepoDetails from(GithubRepoResponse response) {
		return new GithubRepoDetails(response.fullName(), response.owner().login(), response.stargazersCount());
	}
}
