package com.moyo.backend.domain.batch.ranking.dto;

public record GithubRepoDetails(
	String repoName,
	String ownerName,
	int starCount
) {

	public static GithubRepoDetails from(GithubRepoDetailsResponse response) {
		return new GithubRepoDetails(response.repoFullName(), response.owner().name(), response.startCount());
	}
}
