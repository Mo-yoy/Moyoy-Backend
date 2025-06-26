package com.moyo.backend.batch.ranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RankingPreflight(
	@JsonProperty("login") String username,
	@JsonProperty("followers") int followers) {

}
