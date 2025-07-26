package com.moyo.backend.domain.batch.ranking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moyo.backend.common.implement.GithubOAuthTokenReader;
import com.moyo.backend.domain.batch.ranking.dto.GithubCommitStats;
import com.moyo.backend.domain.batch.ranking.dto.GithubRepoDetails;
import com.moyo.backend.domain.batch.ranking.dto.RankingPreflight;
import com.moyo.backend.domain.batch.ranking.processor.GithubRepoClassifier;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculator;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculatorParameters;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculatorResult;
import com.moyo.backend.domain.batch.ranking.processor.RankingMetricsCalculator;
import com.moyo.backend.domain.batch.ranking.reader.RankingBatchReader;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingReader;
import com.moyo.backend.domain.github_ranking.implement.RankingUpdater;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserReader;

@Slf4j
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

	///  임시 랭킹 산출
	@Deprecated
	@Scheduled(cron = "0 52 21 * * *")
	public void rankingBatchScheduler() {

		log.info("{} 랭킹 배치 작업 시작!", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

		///  TODO : User 전체 불러오기 개선 필요
		List<User> userList = userReader.findAll();
		List<Long> userIdList = userList.stream().map(User::getId).toList();
		List<Integer> githubUserIdList = userList.stream().map(User::getGithubUserId).toList();

		for (int idx = 0; idx < userList.size(); idx++) {

			Long userId = userIdList.get(idx);
			Integer githubUserId = githubUserIdList.get(idx);

			// 현재 UserId로 DB에 저장된 사용자의 깃허브 토큰을 받아 온다.
			String githubAccessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

			// 1. 사용자 id로 username, follower 수, 소유 중인 개인 Repo 수, RateLimitRemaining 체크
			RankingPreflight rankingPreflight = rankingBatchReader.getRankingPreflight(githubUserId, githubAccessToken);
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
