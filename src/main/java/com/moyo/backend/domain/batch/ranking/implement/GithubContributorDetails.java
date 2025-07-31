package com.moyo.backend.domain.batch.ranking.implement;

import com.moyo.backend.domain.batch.ranking.data_access.GithubContributorDetailsResponse;

public record GithubContributorDetails(
	String username) {
	public static GithubContributorDetails from(GithubContributorDetailsResponse githubContributorDetailsResponse) {
		return new GithubContributorDetails(githubContributorDetailsResponse.username());
	}
}
