package com.moyo.backend.domain.batch.ranking.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moyo.backend.common.implement.GithubOAuthTokenReader;
import com.moyo.backend.domain.batch.ranking.dto.UserBatchSnapshot;
import com.moyo.backend.domain.batch.ranking.processor.CommitStatCalculator;
import com.moyo.backend.domain.batch.ranking.processor.GithubRepoClassifier;
import com.moyo.backend.domain.batch.ranking.processor.RankingCalculator;
import com.moyo.backend.domain.batch.ranking.reader.RankingBatchReader;
import com.moyo.backend.domain.batch.ranking.service.RankingBatchExecutorService;
import com.moyo.backend.domain.batch.ranking.service.RankingBatchTask;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingReader;
import com.moyo.backend.domain.github_ranking.implement.RankingUpdater;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

	private static final int RANKING_BATCH_PAGE_SIZE = 100;

	private final UserReader userReader;
	private final RankingBatchReader rankingBatchReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final RankingReader rankingReader;
	private final GithubRepoClassifier githubRepoClassifier;
	private final RankingCalculator rankingCalculator;
	private final RankingUpdater rankingUpdater;
	private final CommitStatCalculator commitStatCalculator;
	private final RankingBatchExecutorService rankingBatchExecutorService;


	@Scheduled(cron = "10 47 20 * * *")
	public void rankingBatch() {

		LocalDateTime now = LocalDateTime.now();
		log.info("{} 랭킹 배치 작업 시작!", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

		UserBatchSnapshot userBatchSnapshot = userReader.getUserBatchSnapshot();
		log.info("{} 랭킹 배치 작업 | 총 유저 수 : {}", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), userBatchSnapshot.userCount());
		log.info("총 페이지 수: {}", userBatchSnapshot.userCount() / RANKING_BATCH_PAGE_SIZE + 1);

		long lastUserId = 0;

		///  모든 User를 한 번에 메모리에 올릴 수는 없음
		while(lastUserId < userBatchSnapshot.lastUserId()){

			List<User> userList = userReader.findAll(lastUserId, RANKING_BATCH_PAGE_SIZE);
			lastUserId = userList.getLast().getId();

			List<Callable<Ranking>> rankingBatchTasks = new ArrayList<>();

			for (User user : userList) {

				RankingBatchTask rankingBatchTask = new RankingBatchTask(
					user,
					githubOAuthTokenReader,
					rankingBatchReader,
					githubRepoClassifier,
					commitStatCalculator,
					rankingCalculator,
					rankingReader
				);

				rankingBatchTasks.add(rankingBatchTask);
			}
			List<Future<Ranking>> futures = rankingBatchExecutorService.submitAll(rankingBatchTasks);
			List<Ranking> updatedRankings = new ArrayList<>();

			futures.forEach(future -> {
				try {
					Ranking ranking = future.get();
					updatedRankings.add(ranking);
				} catch (Exception e) {
					log.warn("랭킹 계산 실패", e);
					// 실패 누적/통계 기록 등 추가로 가능
				}
			});

			// 7. 랭킹 업데이트
			/// TODO : 동작 방식 생각해 보면 원래 의도와 다르다는 것을 알 수 있음. 리팩토링 대상. 
			/// 원래 하려던 것 : DB와 한번만 통신하면서 벌크성 업데이트, 지금 동작 : 계속 통신
			rankingUpdater.updateAll(updatedRankings);
		}
	}

}
