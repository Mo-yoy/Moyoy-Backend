package com.moyo.backend.domain.temporary_batch;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class ContributorStats {

	@JsonProperty("author")
	private Author author;

	@JsonProperty("weeks")
	private List<Week> weeks;

	@Getter
	@Setter
	public static class Author {

		@JsonProperty("login")
		private String username;
	}

	@Getter
	@Setter
	public static class Week {

		@JsonProperty("w")
		private long weekTimeStamp;

		@JsonProperty("a")
		private int addCodeLine;

		@JsonProperty("c")
		private int commit;
	}
}
