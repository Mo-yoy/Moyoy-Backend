package com.moyo.backend.domain.batch.ranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubContributorDetails(
	@JsonProperty("login") String username) {
}
