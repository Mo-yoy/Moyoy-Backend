package com.moyo.backend.ranking.batch;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RankingPreflight(
	@JsonProperty("login") String username,
	@JsonProperty("followers") int followers) {

}
