package com.moyoy.batch.domain.ranking.step.dto;

import com.moyoy.batch.domain.ranking.component.dto.GithubCommitStats;
import com.moyoy.batch.domain.ranking.component.dto.GithubRepoDetails;

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

	public static RankingCalculatorParameters from(RankingDataResult rankingDataResult) {

		int stars = rankingDataResult.rankingCandidateRepos().stream()
			.mapToInt(GithubRepoDetails::starCount)
			.sum();

		int followers = rankingDataResult.userRankingProfile().followerCount();

		GithubCommitStats commitStats = rankingDataResult.commitStats();
		CommitStatsSummary weekStats = new CommitStatsSummary(commitStats.weekStats().commits(), commitStats.weekStats().commitLines());
		CommitStatsSummary monthStats = new CommitStatsSummary(commitStats.monthStats().commits(), commitStats.monthStats().commitLines());
		CommitStatsSummary yearStats = new CommitStatsSummary(commitStats.yearStats().commits(), commitStats.yearStats().commitLines());

		return new RankingCalculatorParameters(stars, followers, weekStats, monthStats, yearStats);
	}
}
