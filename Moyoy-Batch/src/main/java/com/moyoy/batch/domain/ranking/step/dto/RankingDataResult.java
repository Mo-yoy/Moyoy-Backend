package com.moyoy.batch.domain.ranking.step.dto;

import java.util.List;

import com.moyoy.batch.domain.ranking.component.dto.GithubCommitStats;
import com.moyoy.batch.domain.ranking.component.dto.GithubRepoDetails;
import com.moyoy.batch.domain.ranking.component.dto.UserRankingProfile;

public record RankingDataResult(
	List<GithubRepoDetails> rankingCandidateRepos,
	UserRankingProfile userRankingProfile,
	GithubCommitStats commitStats) {
	public static RankingDataResult of(List<GithubRepoDetails> rankingCandidateRepos,
		UserRankingProfile userRankingProfile, GithubCommitStats commitStats) {
		return new RankingDataResult(rankingCandidateRepos, userRankingProfile, commitStats);
	}
}
