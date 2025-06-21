package com.moyo.backend.domain.github_ranking.batch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubContributorDetails(
	@JsonProperty("login") String username) {
}
