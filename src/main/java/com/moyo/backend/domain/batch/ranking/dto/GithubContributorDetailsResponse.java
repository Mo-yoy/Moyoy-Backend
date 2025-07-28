package com.moyo.backend.domain.batch.ranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubContributorDetailsResponse(
	@JsonProperty("login") String username) {
}
