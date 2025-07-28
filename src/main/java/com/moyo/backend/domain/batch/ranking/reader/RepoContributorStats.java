package com.moyo.backend.domain.batch.ranking.reader;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RepoContributorStats(
	Author author,
	List<Week> weeks
) {
	public record Author(
		@JsonProperty("login") String username
	) {}

	public record Week(
		@JsonProperty("w") long weekTimeStamp,
		@JsonProperty("a") int addCodeLine,
		@JsonProperty("c") int commit
	) {}
}

