package com.moyo.backend.domain.temporary_batch.ranking;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moyo.backend.common.implement.GithubOAuthTokenReader;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingUpdater;
import com.moyo.backend.domain.temporary_batch.GithubCommitStats;
import com.moyo.backend.domain.temporary_batch.GithubRepoDetails;
import com.moyo.backend.domain.temporary_batch.ranking.processor.RankingCalculator;
import com.moyo.backend.domain.temporary_batch.ranking.dto.RankingPreflight;
import com.moyo.backend.domain.temporary_batch.ranking.dto.RankingCalculatorParameters;
import com.moyo.backend.domain.temporary_batch.ranking.processor.GithubRepoClassifier;
import com.moyo.backend.domain.temporary_batch.ranking.processor.RankingMetricsCalculator;
import com.moyo.backend.domain.temporary_batch.ranking.reader.RankingBatchReader;
import com.moyo.backend.domain.user.implement.UserReader;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

	private final UserReader userReader;
	private final RankingBatchReader rankingBatchReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;

	private final GithubRepoClassifier githubRepoClassifier;
	private final RankingMetricsCalculator rankingMetricsCalculator;
	private final RankingCalculator rankingCalculator;

	private final RankingUpdater rankingUpdater;

	@Scheduled(cron = "0 15 17 * * *")
	public void rankingBatchScheduler() {

		System.out.println("Ranking 배치 시작");
		
		// 모든 유저의 Id를 읽어온다.
		List<Long> userIds = userReader.findAllUserIdList();

		for (Long userId : userIds) {

			System.out.println("현재 유저의 깃허브 사용자 토큰을 받아 온다.");
			// 현재 유저의 깃허브 사용자 토큰을 받아 온다.
			String githubAccessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

			System.out.println(1);
			// 1. 사용자 id로 username, follower 수, 소유 중인 개인 Repo 수, RateLimitRemaining 체크
			RankingPreflight rankingPreflight = rankingBatchReader.getRankingPreflight(userId, githubAccessToken);
			String username = rankingPreflight.username();

			System.out.println(2);
			// 2. 해당 사용자의 2025년 Repo를 모두 가져옴
			List<GithubRepoDetails> allGithubRepoDetailsList = rankingBatchReader.getGithubRepoDetails(githubAccessToken);

			System.out.println(3);
			// 3. 사용자 Own Repo + 사용자가 속한 Org의 Repo 중 실제 기여한 Repo만 추출
			List<GithubRepoDetails> githubRepoClassifyResult = githubRepoClassifier.classify(allGithubRepoDetailsList, username, githubAccessToken);

			System.out.println(4);
			// 4. 최종적으로 필터링 된 Repo들 중에서 커밋 관련 데이터 획득
			List<GithubCommitStats> commitStats = new ArrayList<>();
			for (GithubRepoDetails repoDetails : githubRepoClassifyResult) {

				GithubCommitStats githubCommitStats = rankingBatchReader.getGithubCommitStats(repoDetails.repoFullName(), username, githubAccessToken);
				commitStats.add(githubCommitStats);
			}

			System.out.println(5);
			// 5. 파라미터 획득
			RankingCalculatorParameters calculatorParameters = rankingMetricsCalculator.calculateParameters(githubRepoClassifyResult, rankingPreflight, commitStats);

			System.out.println(6);
			// 6. 랭킹 계산 , 이 부분은 아직 좀 손봐야 함. 임시
			int starCount = calculatorParameters.stars();
			int followerCount = calculatorParameters.followers();

			long weekPoint = rankingCalculator.calculateRanking(calculatorParameters.weekStats().commits(), calculatorParameters.weekStats().commitLines(), starCount, followerCount).points();
			long monthPoint = rankingCalculator.calculateRanking(calculatorParameters.monthStats().commits(), calculatorParameters.monthStats().commitLines(), starCount, followerCount).points();
			long yearPoint = rankingCalculator.calculateRanking(calculatorParameters.yearStats().commits(), calculatorParameters.yearStats().commitLines(), starCount, followerCount).points();
			String grade = rankingCalculator.calculateRanking(calculatorParameters.yearStats().commits(), calculatorParameters.yearStats().commitLines(), starCount, followerCount).level();

			System.out.println(7);
			// 7. 랭킹 업데이트
			Ranking ranking = new Ranking(userId, null, grade, weekPoint, monthPoint, yearPoint);
			rankingUpdater.update(ranking);
		}
	}

}
