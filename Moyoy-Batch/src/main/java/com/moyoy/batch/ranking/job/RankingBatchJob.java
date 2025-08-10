package com.moyoy.batch.ranking.job;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.batch.jobRepository.ranking.RankingBatchHistory;
import com.moyoy.batch.ranking.component.dto.RankingBatchStats;
import com.moyoy.batch.ranking.step.dto.RankingCalculatorParameters;
import com.moyoy.batch.ranking.step.dto.RankingCalculatorResult;
import com.moyoy.batch.ranking.step.RankingCalculatorStep;
import com.moyoy.batch.ranking.step.RankingDataFetcherStep;
import com.moyoy.batch.ranking.step.dto.RankingDataResult;
import com.moyoy.batch.ranking.step.dto.RankingUpdateParameters;
import com.moyoy.batch.ranking.step.RankingUpdatePreparationStep;
import com.moyoy.domain.user.implement.User;
import com.moyoy.domain.user.implement.UserReader;
import com.moyoy.domain.user.implement.UserStats;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchJob {

	public static final int RANKING_BATCH_CHUNK_SIZE = 5;

	private final UserReader userReader;
	private final RankingDataFetcherStep rankingDataFetcherStep;
	private final RankingCalculatorStep rankingCalculatorStep;
	private final RankingUpdatePreparationStep rankingUpdatePreparationStep;
	private final RankingBatchResultWriter rankingBatchResultWriter;
	private final RankingBatchHistoryUpdater rankingBatchHistoryUpdater;

	public void execute(RankingBatchHistory rankingBatchHistory) {

		UserStats userStats = userReader.getUserStats();

		List<RankingBatchStats> rankingBatchStatsList = new ArrayList<>();
		long userIdCursor = 0;
		long lastUserId = userStats.lastUserId();

		while (userIdCursor < lastUserId) {

			List<User> userList = userReader.findAll(userIdCursor, RANKING_BATCH_CHUNK_SIZE);
			List<RankingBatchResult> rankingBatchResultList = new ArrayList<>();

			for (User user : userList) {

				Long currentUserId = user.getId();
				RankingBatchResult rankingBatchResult = RankingBatchResult.init(currentUserId);

				RankingDataResult rankingDataResult = rankingDataFetcherStep.execute(user);
				RankingCalculatorParameters rankingCalculatorParameters = RankingCalculatorParameters.from(rankingDataResult);

				RankingCalculatorResult rankingCalculatorResult = rankingCalculatorStep.execute(rankingCalculatorParameters);

				RankingUpdateParameters rankingUpdateParameters = RankingUpdateParameters.of(currentUserId, rankingCalculatorResult);

				rankingUpdatePreparationStep.execute(rankingUpdateParameters, rankingBatchResult);
				rankingBatchResultList.add(rankingBatchResult);
			}

			RankingBatchStats rankingBatchStats = rankingBatchResultWriter.collectResultsAndUpdate(rankingBatchHistory.getId(), rankingBatchResultList);
			rankingBatchStatsList.add(rankingBatchStats);

			userIdCursor = userList.getLast().getId();
		}

		rankingBatchHistoryUpdater.updateFinalResult(rankingBatchHistory, rankingBatchStatsList);
	}
}
