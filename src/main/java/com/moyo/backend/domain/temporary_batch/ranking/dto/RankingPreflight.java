package com.moyo.backend.domain.temporary_batch.ranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RankingPreflight(
	@JsonProperty("login") String username,
	@JsonProperty("followers") int followers) {

}
