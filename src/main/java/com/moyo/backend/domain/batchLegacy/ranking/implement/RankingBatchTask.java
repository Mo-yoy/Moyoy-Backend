package com.moyo.backend.domain.batchLegacy.ranking.implement;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.moyo.backend.common.exception.MoyoException;
import com.moyo.backend.common.implement.GithubOAuthTokenReader;
import com.moyo.backend.domain.batchLegacy.ranking.data_access.RepoContributorStats;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingReader;
import com.moyo.backend.domain.user.implement.User;

@Slf4j
@Builder
@RequiredArgsConstructor
public class RankingBatchTask implements Callable<RankingBatchTaskResult> {

	private final User user;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final RankingBatchReader rankingBatchReader;
	private final GithubRepoClassifier githubRepoClassifier;
	private final CommitStatCalculator commitStatCalculator;
	private final RankingCalculator rankingCalculator;
	private final RankingReader rankingReader;

	@Override
	public RankingBatchTaskResult call() {

		try {
			Long currentUserId = user.getId();
			Integer currentGithubUserId = user.getGithubUserId();
			String githubAccessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

			// 1. 사용자 id로 username, follower 수, 소유 중인 개인 Repo 수, RateLimitRemaining 체크
			UserRankingBatchSnapshot.RankingPreflight rankingPreflight = rankingBatchReader.fetchRankingPreflight(currentGithubUserId, githubAccessToken);
			rankingPreflight.assertCanGithubRequest();

			// 2. 해당 사용자가 read, write, owner 권한을 가지고 있는 올해 Repo 모두 가져옴
			List<GithubRepoDetails> githubRepoDetailsList = rankingBatchReader.fetchAllGithubRepoDetails(githubAccessToken);

			// 3. Repo 중에서 사용자 소유의 Repo와 사용자가 기여한 Repo 선별
			String currentUsername = rankingPreflight.username();
			List<GithubRepoDetails> rankingCandidateRepos = githubRepoClassifier.classify(githubRepoDetailsList, currentUsername, githubAccessToken);

			// 4. 최종 필터링 된 Repo들 중에서 커밋 관련 데이터 획득 (엄청난 I/O 병목이 발생하는 구간)
			List<RepoContributorStats> userRepoContributorStatsList = new ArrayList<>();

			for (GithubRepoDetails repoDetails : rankingCandidateRepos) {

				List<RepoContributorStats> repoContributorStatsList = rankingBatchReader.fetchRepoContributorStats(repoDetails.repoName(), githubAccessToken);

				repoContributorStatsList.stream()
					.filter(contributor -> contributor.author().username().equals(currentUsername))
					.findFirst()
					.ifPresent(userRepoContributorStatsList::add);
			}

			GithubCommitStats commitStats = commitStatCalculator.calculateCommitStats(userRepoContributorStatsList);

			// 5. 파라미터 생성
			RankingCalculatorParameters rankingCalculatorParameters = RankingCalculatorParameters.of(rankingCandidateRepos, rankingPreflight, commitStats);

			// 6. 랭킹 계산
			RankingCalculatorResult rankingCalculatorResult = rankingCalculator.calculate(rankingCalculatorParameters);

			Ranking ranking = rankingReader.getRanking(currentUserId);
			ranking.updateRankingByBatch(rankingCalculatorResult);

			return RankingBatchTaskResult.success(currentUserId, ranking);
		} catch (MoyoException e) {

			return RankingBatchTaskResult.fail(user.getId(), e.getErrorReason().getErrorMessage());
		} catch (Exception e) {

			String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
			log.error("{}의 랭킹 배치 중 알 수 없는 에러 발생", user.getId(), e);
			return RankingBatchTaskResult.fail(user.getId(), "Unknown Error || Error Log Time : " + timestamp);
		}
	}
}
