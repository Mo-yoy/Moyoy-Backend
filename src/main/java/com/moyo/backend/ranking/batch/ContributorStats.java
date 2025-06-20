package com.moyo.backend.ranking.batch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContributorStats {

	@JsonProperty("author")
	private Author author;

	@JsonProperty("weeks")
	private List<Week> weeks;

	@Getter
	@Setter
	static class Author {

		@JsonProperty("login")
		private String username;
	}

	@Getter
	@Setter
	static class Week {

		@JsonProperty("w")
		private long weekTimeStamp;

		@JsonProperty("a")
		private int addCodeLine;

		@JsonProperty("c")
		private int commit;
	}
}
