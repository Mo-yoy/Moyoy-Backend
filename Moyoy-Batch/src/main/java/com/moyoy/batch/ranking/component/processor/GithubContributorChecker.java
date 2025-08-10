package com.moyoy.batch.ranking.component.processor;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.batch.ranking.component.dto.GithubContributorDetails;
import com.moyoy.batch.ranking.component.reader.RankingBatchReader;

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
