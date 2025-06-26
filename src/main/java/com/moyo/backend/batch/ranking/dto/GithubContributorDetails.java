package com.moyo.backend.batch.ranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubContributorDetails(
	@JsonProperty("login") String username) {
}
