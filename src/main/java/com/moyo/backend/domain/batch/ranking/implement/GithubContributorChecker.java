package com.moyo.backend.domain.batch.ranking.implement;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.dto.GithubContributorDetails;

@Component
@RequiredArgsConstructor
public class GithubContributorChecker {

	private final RankingBatchReader rankingBatchReader;

	public boolean isContributor(String repoName, String targetUsername, String accessToken) {

		List<GithubContributorDetails> contributors = rankingBatchReader.fetchRepoContributors(repoName, accessToken);

		return contributors.stream()
			.anyMatch(c -> c.username().equals(targetUsername));
	}
}
