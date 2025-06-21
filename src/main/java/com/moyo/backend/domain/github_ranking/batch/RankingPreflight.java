package com.moyo.backend.domain.github_ranking.batch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RankingPreflight(
	@JsonProperty("login") String username,
	@JsonProperty("followers") int followers) {

}
