package com.moyoy.infra.external.github.repo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepoCommitStatsResponse(
	Author author,
	List<Week> weeks) {
	public record Author(
		@JsonProperty("login") String login) {
	}

	public record Week(
		@JsonProperty("w") long w,
		@JsonProperty("a") int a,
		@JsonProperty("d") int d,
		@JsonProperty("c") int c
	) {}
}
