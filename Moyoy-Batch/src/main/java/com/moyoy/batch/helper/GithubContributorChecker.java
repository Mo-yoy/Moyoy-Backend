package com.moyoy.batch.helper;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.batch.legacy.domain.ranking.component.dto.GithubContributorDetails;
import com.moyoy.batch.legacy.domain.ranking.component.reader.RankingBatchReader;

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
