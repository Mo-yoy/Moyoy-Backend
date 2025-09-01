package com.moyoy.batch.legacy.domain.ranking.job;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.batch.legacy.domain.ranking.component.dto.RankingBatchResult;
import com.moyoy.batch.legacy.domain.ranking.component.dto.RankingBatchStats;
import com.moyoy.batch.legacy.domain.ranking.component.writer.RankingBatchResultWriter;
import com.moyoy.batch.legacy.domain.ranking.step.RankingCalculatorStep;
import com.moyoy.batch.legacy.domain.ranking.step.RankingDataFetcherStep;
import com.moyoy.batch.legacy.domain.ranking.step.RankingUpdatePreparationStep;
import com.moyoy.domain.ranking.RankingCalculatorParameters;
import com.moyoy.domain.ranking.RankingCalculatorResult;
import com.moyoy.batch.legacy.domain.ranking.step.dto.RankingDataResult;
import com.moyoy.batch.legacy.domain.ranking.step.dto.RankingUpdateParameters;
import com.moyoy.batch.legacy.jobRepository.ranking.RankingBatchHistory;
import com.moyoy.batch.legacy.jobRepository.ranking.RankingBatchHistoryUpdater;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchJob {

	public static final int RANKING_BATCH_CHUNK_SIZE = 5;

	private final UserRepository userRepository;
	private final RankingDataFetcherStep rankingDataFetcherStep;
	private final RankingCalculatorStep rankingCalculatorStep;
	private final RankingUpdatePreparationStep rankingUpdatePreparationStep;
	private final RankingBatchResultWriter rankingBatchResultWriter;
	private final RankingBatchHistoryUpdater rankingBatchHistoryUpdater;

	public void execute(RankingBatchHistory rankingBatchHistory) {

		UserFetchSummary userFetchSummary = userRepository.fetchUserCountAndLastId();

		List<RankingBatchStats> rankingBatchStatsList = new ArrayList<>();
		long userIdCursor = 0;
		long lastUserId = userFetchSummary.lastUserId();

		while (userIdCursor < lastUserId) {

			List<User> userList = userRepository.findAll(userIdCursor, RANKING_BATCH_CHUNK_SIZE);
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
