package com.moyoy.batch.dto;

import java.util.List;

import com.moyoy.infra.external.github.repo.GithubRepoCommitStatsResponse;

public record GithubRepoCommitStats(
	Author author,
	List<Week> weeks
) {

	public record Author(
		String username
	) {

	}

	public record Week(
		long weekTimeStamp,
		int addCodeLine,
		int commit
	) {

	}

	public static GithubRepoCommitStats from(GithubRepoCommitStatsResponse response) {

		return new GithubRepoCommitStats(
			new Author(response.author().login()),
			response.weeks().stream()
				.map(w -> new Week(w.w(), w.a(), w.c()))
				.toList()
		);
	}
}