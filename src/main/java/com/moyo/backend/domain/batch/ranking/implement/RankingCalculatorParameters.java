package com.moyo.backend.domain.batch.ranking.implement;

import java.util.List;

import com.moyo.backend.domain.batch.ranking.dto.GithubCommitStats;
import com.moyo.backend.domain.batch.ranking.dto.GithubRepoDetails;
import com.moyo.backend.domain.batch.ranking.dto.RankingPreflight;

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

	public static RankingCalculatorParameters of(
		List<GithubRepoDetails> repos,
		RankingPreflight rankingPreflight,
		GithubCommitStats commitStats) {

		int stars = repos.stream()
			.mapToInt(GithubRepoDetails::starCount)
			.sum();

		int followers = rankingPreflight.followerCount();

		RankingCalculatorParameters.CommitStatsSummary weekStats = new RankingCalculatorParameters.CommitStatsSummary(commitStats.weekStats().commits(), commitStats.weekStats().commitLines());

		RankingCalculatorParameters.CommitStatsSummary monthStats = new RankingCalculatorParameters.CommitStatsSummary(commitStats.monthStats().commits(), commitStats.monthStats().commitLines());

		RankingCalculatorParameters.CommitStatsSummary yearStats = new RankingCalculatorParameters.CommitStatsSummary(commitStats.yearStats().commits(), commitStats.yearStats().commitLines());

		return new RankingCalculatorParameters(stars, followers, weekStats, monthStats, yearStats);
	}
}
