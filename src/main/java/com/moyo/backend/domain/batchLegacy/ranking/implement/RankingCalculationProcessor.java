package com.moyo.backend.domain.batchLegacy.ranking.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyo.backend.common.implement.GithubOAuthTokenReader;
import com.moyo.backend.domain.github_ranking.implement.RankingReader;
import com.moyo.backend.domain.user.implement.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingCalculationProcessor {

	private final RankingBatchReader rankingBatchReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final RankingReader rankingReader;
	private final GithubRepoClassifier githubRepoClassifier;
	private final RankingCalculator rankingCalculator;
	private final CommitStatCalculator commitStatCalculator;
	private final RankingBatchExecutor rankingBatchExecutor;

	public List<Future<RankingBatchTaskResult>> calculateRanking(List<User> userList) {

		List<Callable<RankingBatchTaskResult>> rankingBatchTasks = new ArrayList<>();

		userList.stream()
			.map(user -> RankingBatchTask.builder()
				.user(user)
				.githubOAuthTokenReader(githubOAuthTokenReader)
				.rankingBatchReader(rankingBatchReader)
				.githubRepoClassifier(githubRepoClassifier)
				.commitStatCalculator(commitStatCalculator)
				.rankingCalculator(rankingCalculator)
				.rankingBatchReader(rankingBatchReader)
				.rankingReader(rankingReader)
				.build())
			.forEach(rankingBatchTasks::add);

		return rankingBatchExecutor.invokeAll(rankingBatchTasks);
	}
}
