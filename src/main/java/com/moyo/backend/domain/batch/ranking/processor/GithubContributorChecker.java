package com.moyo.backend.domain.batch.ranking.processor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.dto.GithubContributorDetails;
import com.moyo.backend.domain.batch.ranking.reader.RankingBatchReader;

import lombok.RequiredArgsConstructor;

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
