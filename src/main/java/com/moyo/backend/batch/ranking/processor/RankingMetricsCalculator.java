package com.moyo.backend.batch.ranking.processor;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.batch.ranking.dto.GithubCommitStats;
import com.moyo.backend.batch.ranking.dto.GithubRepoDetails;
import com.moyo.backend.batch.ranking.dto.RankingCalculatorParameters;
import com.moyo.backend.batch.ranking.dto.RankingPreflight;

@Component
@RequiredArgsConstructor
public class RankingMetricsCalculator {

	public RankingCalculatorParameters calculateParameters(
		List<GithubRepoDetails> repos,
		RankingPreflight rankingPreflight,
		List<GithubCommitStats> commitStats) {

		int stars = getStarCount(repos);
		int followers = getFollowerCount(rankingPreflight);

		RankingCalculatorParameters.CommitStatsSummary weekStats = new RankingCalculatorParameters.CommitStatsSummary(
			getWeekCommitCount(commitStats),
			getWeekCommitLines(commitStats));

		RankingCalculatorParameters.CommitStatsSummary monthStats = new RankingCalculatorParameters.CommitStatsSummary(
			getMonthCommitCount(commitStats),
			getMonthCommitLines(commitStats));

		RankingCalculatorParameters.CommitStatsSummary yearStats = new RankingCalculatorParameters.CommitStatsSummary(
			getYearCommitCount(commitStats),
			getYearCommitLines(commitStats));

		return new RankingCalculatorParameters(stars, followers, weekStats, monthStats, yearStats);
	}

	private int getStarCount(List<GithubRepoDetails> repos) {
		return repos.stream()
			.mapToInt(GithubRepoDetails::startCount)
			.sum();
	}

	private int getFollowerCount(RankingPreflight rankingPreflight) {
		return rankingPreflight.followers();
	}

	private int getWeekCommitCount(List<GithubCommitStats> commitStats) {
		return commitStats.stream()
			.mapToInt(cs -> cs.weekStats().commits())
			.sum();
	}

	private int getWeekCommitLines(List<GithubCommitStats> commitStats) {
		return commitStats.stream()
			.mapToInt(cs -> cs.weekStats().commitLines())
			.sum();
	}

	private int getMonthCommitCount(List<GithubCommitStats> commitStats) {
		return commitStats.stream()
			.mapToInt(cs -> cs.monthStats().commits())
			.sum();
	}

	private int getMonthCommitLines(List<GithubCommitStats> commitStats) {
		return commitStats.stream()
			.mapToInt(cs -> cs.monthStats().commitLines())
			.sum();
	}

	private int getYearCommitCount(List<GithubCommitStats> commitStats) {
		return commitStats.stream()
			.mapToInt(cs -> cs.yearStats().commits())
			.sum();
	}

	private int getYearCommitLines(List<GithubCommitStats> commitStats) {
		return commitStats.stream()
			.mapToInt(cs -> cs.yearStats().commitLines())
			.sum();
	}
}
