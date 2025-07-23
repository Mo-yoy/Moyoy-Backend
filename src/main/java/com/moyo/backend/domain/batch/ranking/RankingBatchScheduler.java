package com.moyo.backend.domain.batch.ranking;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.dto.GithubCommitStats;
import com.moyo.backend.domain.batch.ranking.dto.GithubRepoDetails;
import com.moyo.backend.domain.batch.ranking.dto.RankingPreflight;
import com.moyo.backend.domain.batch.ranking.processor.GithubRepoClassifier;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculator;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculatorParameters;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculatorResult;
import com.moyo.backend.domain.batch.ranking.processor.RankingMetricsCalculator;
import com.moyo.backend.domain.batch.ranking.reader.RankingBatchReader;
import com.moyo.backend.common.implement.GithubOAuthTokenReader;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingReader;
import com.moyo.backend.domain.github_ranking.implement.RankingUpdater;
import com.moyo.backend.domain.user.implement.UserReader;

@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

	private final UserReader userReader;
	private final RankingBatchReader rankingBatchReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final RankingReader rankingReader;

	private final GithubRepoClassifier githubRepoClassifier;
	private final RankingMetricsCalculator rankingMetricsCalculator;
	private final RankingCalculator rankingCalculator;

	private final RankingUpdater rankingUpdater;

	// @Scheduled(cron = "0 10 21 * * *")
	public void rankingBatchScheduler() {

		System.out.println("Ranking 배치 시작");

		// 모든 유저의 Id를 읽어온다.
		List<Long> userIds = userReader.findAllUserIdList();
		userIds.forEach(System.out::println);

		for (Long userId : userIds) {

			System.out.println("현재 유저의 깃허브 사용자 토큰을 받아 온다.");
			// 현재 유저의 깃허브 사용자 토큰을 받아 온다.
			String githubAccessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

			// 1. 사용자 id로 username, follower 수, 소유 중인 개인 Repo 수, RateLimitRemaining 체크
			RankingPreflight rankingPreflight = rankingBatchReader.getRankingPreflight(userId, githubAccessToken);
			String username = rankingPreflight.username();

			// 2. 해당 사용자의 2025년 Repo를 모두 가져옴
			List<GithubRepoDetails> allGithubRepoDetailsList = rankingBatchReader.getGithubRepoDetails(githubAccessToken);

			// 3. 사용자 Own Repo + 사용자가 속한 Org의 Repo 중 실제 기여한 Repo만 추출
			List<GithubRepoDetails> githubRepoClassifyResult = githubRepoClassifier.classify(allGithubRepoDetailsList, username, githubAccessToken);

			// 4. 최종적으로 필터링 된 Repo들 중에서 커밋 관련 데이터 획득
			List<GithubCommitStats> commitStats = new ArrayList<>();
			for (GithubRepoDetails repoDetails : githubRepoClassifyResult) {

				GithubCommitStats githubCommitStats = rankingBatchReader.getGithubCommitStats(repoDetails.repoFullName(), username, githubAccessToken);
				commitStats.add(githubCommitStats);
			}

			// 5. 파라미터 획득
			RankingCalculatorParameters rankingParameters = rankingMetricsCalculator.calculateParameters(githubRepoClassifyResult, rankingPreflight, commitStats);

			// 6. 랭킹 계산
			RankingCalculatorResult rankingCalculatorResult = rankingCalculator.calculate(rankingParameters);

			// 7. 랭킹 업데이트
			Ranking ranking = rankingReader.getRanking(userId);
			ranking.updateRankingByBatch(rankingCalculatorResult);
			rankingUpdater.update(ranking);
		}
	}

}
