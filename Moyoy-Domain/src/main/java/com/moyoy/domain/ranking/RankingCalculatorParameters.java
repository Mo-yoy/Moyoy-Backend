package com.moyoy.domain.ranking;

public record RankingCalculatorParameters(
	int stars,
	int followers,
	CommitStatsSummary weekStats,
	CommitStatsSummary monthStats,
	CommitStatsSummary yearStats) {

	public record CommitStatsSummary(
		int commits,
		int commitLines) {
	}

	public static RankingCalculatorParameters of(int stars, int followers, GithubCommitStats commitStats) {

		CommitStatsSummary weekStats = new CommitStatsSummary(commitStats.weekStats().commits(), commitStats.weekStats().commitLines());
		CommitStatsSummary monthStats = new CommitStatsSummary(commitStats.monthStats().commits(), commitStats.monthStats().commitLines());
		CommitStatsSummary yearStats = new CommitStatsSummary(commitStats.yearStats().commits(), commitStats.yearStats().commitLines());

		return new RankingCalculatorParameters(stars, followers, weekStats, monthStats, yearStats);
	}
}
