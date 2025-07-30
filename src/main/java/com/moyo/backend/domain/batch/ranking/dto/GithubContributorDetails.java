package com.moyo.backend.domain.batch.ranking.dto;

public record GithubContributorDetails(
	String username) {
	public static GithubContributorDetails from(GithubContributorDetailsResponse githubContributorDetailsResponse) {
		return new GithubContributorDetails(githubContributorDetailsResponse.username());
	}
}
