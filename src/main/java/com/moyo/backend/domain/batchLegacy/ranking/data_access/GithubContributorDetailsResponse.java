package com.moyo.backend.domain.batchLegacy.ranking.data_access;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubContributorDetailsResponse(
	@JsonProperty("login") String username) {
}
