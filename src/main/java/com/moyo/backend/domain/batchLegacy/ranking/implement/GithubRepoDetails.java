package com.moyo.backend.domain.batchLegacy.ranking.implement;

import com.moyo.backend.domain.batchLegacy.ranking.data_access.GithubRepoDetailsResponse;

public record GithubRepoDetails(
	String repoName,
	String ownerName,
	int starCount) {

	public static GithubRepoDetails from(GithubRepoDetailsResponse response) {
		return new GithubRepoDetails(response.repoFullName(), response.owner().name(), response.startCount());
	}
}
