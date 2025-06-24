package com.moyo.backend.domain.temporary_batch.ranking.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.temporary_batch.ranking.dto.GithubContributorDetails;
import com.moyo.backend.domain.temporary_batch.ranking.dto.GithubRepoDetails;
import com.moyo.backend.domain.temporary_batch.ranking.reader.RankingBatchReader;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GithubRepoClassifier {

	private final RankingBatchReader rankingBatchReader;

	public List<GithubRepoDetails> classify(
		List<GithubRepoDetails> allRepos,
		String currentUsername,
		String accessToken
	) {

		List<GithubRepoDetails> userRepos = allRepos.stream()
			.filter(repo -> repo.owner().name().equals(currentUsername))
			.toList();

		List<GithubRepoDetails> organizationRepos = allRepos.stream()
			.filter(repo -> !repo.owner().name().equals(currentUsername))
			.filter(repo -> {
				List<GithubContributorDetails> contributors = rankingBatchReader.getRepoContributors(repo.repoFullName(), accessToken);
				return contributors.stream().anyMatch(c -> c.username().equals(currentUsername));
			})
			.toList();

		List<GithubRepoDetails> finalRepos = new ArrayList<>(userRepos);
		finalRepos.addAll(organizationRepos);

		return finalRepos;
	}

}
