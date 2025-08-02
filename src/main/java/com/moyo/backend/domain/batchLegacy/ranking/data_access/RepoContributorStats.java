package com.moyo.backend.domain.batchLegacy.ranking.data_access;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RepoContributorStats(
	Author author,
	List<Week> weeks) {
	public record Author(
		@JsonProperty("login") String username) {
	}

	public record Week(
		@JsonProperty("w") long weekTimeStamp,
		@JsonProperty("a") int addCodeLine,
		@JsonProperty("c") int commit) {
	}
}
