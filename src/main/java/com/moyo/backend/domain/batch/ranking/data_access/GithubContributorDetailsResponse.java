package com.moyo.backend.domain.batch.ranking.data_access;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubContributorDetailsResponse(
	@JsonProperty("login") String username) {
}
