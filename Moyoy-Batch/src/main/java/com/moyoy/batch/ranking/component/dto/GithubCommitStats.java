package com.moyoy.batch.ranking.component.dto;

public record GithubCommitStats(
	WeekStats weekStats,
	MonthStats monthStats,
	YearStats yearStats) {

	public record WeekStats(
		int commits,
		int commitLines) {
	}

	public record MonthStats(
		int commits,
		int commitLines) {
	}

	public record YearStats(
		int commits,
		int commitLines) {
	}
}
