package com.moyoy.batch.job.processor;

import java.util.List;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.moyoy.batch.job.processor.dto.GithubContributorDetails;
import com.moyoy.batch.job.processor.dto.GithubRepoDetails;
import com.moyoy.batch.job.processor.dto.RepoCandidatesContext;
import com.moyoy.batch.job.processor.dto.UserProfileContext;

import com.moyoy.infra.external.github.repo.GithubRepoClient;
import com.moyoy.infra.external.github.support.GithubApiLimitChecker;

@Component
@RequiredArgsConstructor
public class PrepareRankingRepoCandidatesProcessor implements ItemProcessor<UserProfileContext, RepoCandidatesContext> {

	private final GithubApiLimitChecker githubApiLimitChecker;
	private final GithubRepoClient githubRepoClient;

	@Override
	public RepoCandidatesContext process(UserProfileContext userContext) {

		String githubAccessToken = userContext.auth().githubAccessToken();
		Long userId = userContext.auth().userId();
		Integer githubUserId = userContext.auth().githubUserId();
		String username = userContext.username();

		githubApiLimitChecker.assertCanGithubRequest(githubAccessToken, userId, githubUserId);

		List<GithubRepoDetails> githubRepoDetailsList = githubRepoClient.fetchReposCreatedThisYear(githubAccessToken)
			.stream()
			.map(GithubRepoDetails::from)
			.toList();

		List<GithubRepoDetails> userOwnedRepos = githubRepoDetailsList.stream()
			.filter(repo -> repo.ownerName().equals(username))
			.toList();

		List<GithubRepoDetails> userContributedRepos = githubRepoDetailsList.stream()
			.filter(repo -> !repo.ownerName().equals(username))
			.filter(repo -> {
				List<GithubContributorDetails> contributors = githubRepoClient.fetchRepoContributors(githubAccessToken, repo.repoFullName())
					.stream()
					.map(GithubContributorDetails::from)
					.toList();
				return contributors.stream().anyMatch(c -> c.username().equals(username));
			})
			.toList();

		List<GithubRepoDetails> candidateRepos = Stream.concat(
			userOwnedRepos.stream(),
			userContributedRepos.stream()).toList();

		return new RepoCandidatesContext(userContext, candidateRepos);
	}
}
