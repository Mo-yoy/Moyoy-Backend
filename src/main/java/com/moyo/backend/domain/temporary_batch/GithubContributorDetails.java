package com.moyo.backend.domain.temporary_batch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubContributorDetails(
	@JsonProperty("login") String username) {
}
