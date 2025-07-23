package com.moyo.backend.domain.batch.ranking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
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

/**
 * @Deprecated 임시 솔루션임, 곧 삭제하고 새로운 버전 만들 예정
 */

@Deprecated(forRemoval = true)
@Component
@RequiredArgsConstructor
public class MultiRankingBatchSchedulerDemo {

	private final UserReader userReader;
	private final RankingBatchReader rankingBatchReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final RankingReader rankingReader;

	private final GithubRepoClassifier githubRepoClassifier;
	private final RankingMetricsCalculator rankingMetricsCalculator;
	private final RankingCalculator rankingCalculator;

	private final RankingUpdater rankingUpdater;

	@Scheduled(cron = "50 09 15 * * *")
	public void rankingBatchScheduler() {
		System.out.println("Ranking 배치 시작");

		List<Long> userIds = userReader.findAllUserIdList();
		userIds.forEach(System.out::println);

		// 동시 10명 제한 (10개의 스레드로 병렬처리)
		ExecutorService executor = Executors.newFixedThreadPool(10);

		for (Long userId : userIds) {
			executor.submit(() -> {
				try {
					processOneUser(userId);
				} catch (Exception e) {
					e.printStackTrace(); // 실패 알림, 로깅 등 처리
				}
			});
		}

		executor.shutdown(); // 더 이상 작업 추가 금지
		try {
			executor.awaitTermination(1, TimeUnit.HOURS); // 1시간 안에 모두 끝나길 대기
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void processOneUser(Long userId) {

		String githubAccessToken = githubOAuthTokenReader.getGithubAccessToken(userId);
		RankingPreflight rankingPreflight = rankingBatchReader.getRankingPreflight(userId, githubAccessToken);
		String username = rankingPreflight.username();

		List<GithubRepoDetails> allGithubRepoDetailsList = rankingBatchReader.getGithubRepoDetails(githubAccessToken);
		List<GithubRepoDetails> githubRepoClassifyResult = githubRepoClassifier.classify(allGithubRepoDetailsList, username, githubAccessToken);

		List<GithubCommitStats> commitStats = new ArrayList<>();
		for (GithubRepoDetails repoDetails : githubRepoClassifyResult) {
			GithubCommitStats githubCommitStats = rankingBatchReader.getGithubCommitStats(repoDetails.repoFullName(), username, githubAccessToken);
			commitStats.add(githubCommitStats);
		}

		RankingCalculatorParameters rankingParameters = rankingMetricsCalculator.calculateParameters(githubRepoClassifyResult, rankingPreflight, commitStats);
		RankingCalculatorResult rankingCalculatorResult = rankingCalculator.calculate(rankingParameters);
		Ranking ranking = rankingReader.getRanking(userId);
		ranking.updateRankingByBatch(rankingCalculatorResult);
		rankingUpdater.update(ranking);
	}
}
