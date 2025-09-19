package com.moyoy.batch.job.processor;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.moyoy.batch.helper.GithubCommitStatCalculator;
import com.moyoy.batch.job.processor.dto.GithubRepoCommitStats;
import com.moyoy.batch.job.processor.dto.GithubRepoDetails;
import com.moyoy.batch.job.processor.dto.RepoCandidatesContext;
import com.moyoy.batch.job.processor.dto.UserSummaryContext;

import com.moyoy.domain.ranking.GithubCommitStats;

import com.moyoy.infra.external.github.repo.GithubRepoClient;
import com.moyoy.infra.external.github.support.GithubApiLimitChecker;
import com.moyoy.infra.external.github.support.error.GithubPollingApiTimeOutException;

@Component
@RequiredArgsConstructor
public class FetchContributorStatsProcessor implements ItemProcessor<RepoCandidatesContext, UserSummaryContext> {

	private final GithubApiLimitChecker githubApiLimitChecker;
	private final GithubRepoClient githubRepoClient;
	private final GithubCommitStatCalculator githubCommitStatCalculator;

	@Override
	public UserSummaryContext process(RepoCandidatesContext repoCandidatesContext) throws GithubPollingApiTimeOutException {
		Long userId = repoCandidatesContext.userProfileContext().auth().userId();
		Integer githubUserId = repoCandidatesContext.userProfileContext().auth().githubUserId();
		String accessToken = repoCandidatesContext.userProfileContext().auth().githubAccessToken();
		String username = repoCandidatesContext.userProfileContext().username();

		githubApiLimitChecker.assertCanGithubRequest(accessToken, userId, githubUserId);

		List<GithubRepoCommitStats> userCommitStats = new ArrayList<>();

		for (GithubRepoDetails repoDetails : repoCandidatesContext.candidateRepos()) {

			List<GithubRepoCommitStats> githubRepoCommitStatsList;

			githubRepoCommitStatsList = githubRepoClient.fetchRepoContributorStats(accessToken, repoDetails.repoFullName())
				.stream()
				.map(GithubRepoCommitStats::from)
				.toList();

			githubRepoCommitStatsList.stream()
				.filter(contributor -> contributor.author().username().equals(username))
				.findFirst()
				.ifPresent(userCommitStats::add);
		}

		GithubCommitStats githubCommitStats = githubCommitStatCalculator.calculateCommitStats(userCommitStats);

		return new UserSummaryContext(repoCandidatesContext, githubCommitStats);
	}
}
