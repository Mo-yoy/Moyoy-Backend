package com.moyoy.batch.legacy.domain.ranking.step;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.GithubCommitStats;
import com.moyoy.batch.dto.GithubRepoDetails;
import com.moyoy.infra.external.github.repo.GithubRepoCommitStatsResponse;
import com.moyoy.batch.legacy.domain.ranking.component.dto.UserRankingProfile;
import com.moyoy.batch.helper.GithubCommitStatCalculator;
import com.moyoy.batch.legacy.domain.ranking.component.processor.GithubRepoClassifier;
import com.moyoy.batch.legacy.domain.ranking.component.reader.RankingBatchReader;
import com.moyoy.batch.legacy.domain.ranking.step.dto.RankingDataResult;
import com.moyoy.domain.user.User;
import com.moyoy.infra.external.github.helper.GithubApiLimitChecker;
import com.moyoy.infra.external.github.helper.GithubOAuthTokenReader;

@Component
@RequiredArgsConstructor
public class RankingDataFetcherStep {

	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final RankingBatchReader rankingBatchReader;
	private final GithubRepoClassifier githubRepoClassifier;
	private final GithubApiLimitChecker githubApiLimitChecker;
	private final GithubCommitStatCalculator githubCommitStatCalculator;

	public RankingDataResult execute(User user) {

		Long currentUserId = user.getId();
		Integer currentGithubUserId = user.getGithubUserId();
		String githubAccessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

		// 1. 사용자 id로 username, follower 수, 소유 중인 개인 Repo 수, RateLimitRemaining 체크
		UserRankingProfile userRankingProfile = rankingBatchReader.fetchUserRankingProfile(githubAccessToken, currentGithubUserId);
		githubApiLimitChecker.assertCanGithubRequest(currentUserId, currentGithubUserId);

		// 2. 해당 사용자가 read, write, owner 권한을 가지고 있는 올해 Repo 모두 가져옴
		List<GithubRepoDetails> githubRepoDetailsList = rankingBatchReader.fetchAllGithubRepoDetails(githubAccessToken);

		// 3. Repo 중에서 사용자 소유의 Repo와 사용자가 기여한 Repo 선별
		String currentUsername = userRankingProfile.username();
		List<GithubRepoDetails> rankingCandidateRepos = githubRepoClassifier.classify(githubRepoDetailsList, currentUsername, githubAccessToken);

		// 4. 최종 필터링 된 Repo들 중에서 커밋 관련 데이터 획득 (엄청난 I/O 병목이 발생하는 구간)
		List<GithubRepoCommitStatsResponse> userGithubRepoCommitStatsResponseList = new ArrayList<>();

		for (GithubRepoDetails repoDetails : rankingCandidateRepos) {

			List<GithubRepoCommitStatsResponse> githubRepoCommitStatsResponseList = rankingBatchReader.fetchRepoContributorStats(repoDetails.repoFullName(), githubAccessToken);

			githubRepoCommitStatsResponseList.stream()
				.filter(contributor -> contributor.author().username().equals(currentUsername))
				.findFirst()
				.ifPresent(userGithubRepoCommitStatsResponseList::add);
		}

		GithubCommitStats commitStats = githubCommitStatCalculator.calculateCommitStats(userGithubRepoCommitStatsResponseList);

		return RankingDataResult.of(rankingCandidateRepos, userRankingProfile, commitStats);
	}
}
