package com.moyo.backend.domain.github_ranking.temporary_batch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepoDetails(
	@JsonProperty("full_name") String repoFullName,
	@JsonProperty("stargazers_count") int startCount,
	@JsonProperty("owner") Owner owner) {

	public record Owner(@JsonProperty("login") String name) {
	}
}
