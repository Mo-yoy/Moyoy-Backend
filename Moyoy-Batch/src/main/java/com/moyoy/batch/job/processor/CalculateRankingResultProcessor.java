package com.moyoy.batch.job.processor;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.moyoy.batch.job.processor.dto.GithubRepoDetails;
import com.moyoy.batch.job.processor.dto.UserProfileContext;
import com.moyoy.batch.job.processor.dto.UserRankResult;
import com.moyoy.batch.job.processor.dto.UserSummaryContext;

import com.moyoy.domain.ranking.GithubCommitStats;
import com.moyoy.domain.ranking.RankingCalculator;
import com.moyoy.domain.ranking.dto.RankingCalculatorParameters;
import com.moyoy.domain.ranking.dto.RankingCalculatorResult;

@Component
@RequiredArgsConstructor
public class CalculateRankingResultProcessor implements ItemProcessor<UserSummaryContext, UserRankResult> {

	private final RankingCalculator rankingCalculator;

	@Override
	public UserRankResult process(UserSummaryContext userSummaryContext) {

		List<GithubRepoDetails> candidateRepos = userSummaryContext.repoCandidatesContext().candidateRepos();
		UserProfileContext userProfileContext = userSummaryContext.repoCandidatesContext().userProfileContext();
		Long userId = userProfileContext.auth().userId();

		int stars = candidateRepos.stream()
			.mapToInt(GithubRepoDetails::starCount)
			.sum();

		int followers = userProfileContext.followerCount();

		GithubCommitStats githubCommitStats = userSummaryContext.githubCommitStats();

		RankingCalculatorParameters rankingCalculatorParameters = RankingCalculatorParameters.of(stars, followers, githubCommitStats);

		RankingCalculatorResult rankingCalculatorResult = rankingCalculator.calculate(rankingCalculatorParameters);

		return new UserRankResult(userId, rankingCalculatorResult);
	}
}
