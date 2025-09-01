package com.moyoy.batch.legacy.domain.ranking.step.dto;

import java.util.List;

import com.moyoy.domain.ranking.GithubCommitStats;
import com.moyoy.batch.dto.GithubRepoDetails;
import com.moyoy.batch.legacy.domain.ranking.component.dto.UserRankingProfile;

public record RankingDataResult(
	List<GithubRepoDetails> rankingCandidateRepos,
	UserRankingProfile userRankingProfile,
	GithubCommitStats commitStats) {
	public static RankingDataResult of(List<GithubRepoDetails> rankingCandidateRepos,
		UserRankingProfile userRankingProfile, GithubCommitStats commitStats) {
		return new RankingDataResult(rankingCandidateRepos, userRankingProfile, commitStats);
	}
}
