package com.moyo.backend.domain.batch.ranking.business;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.implement.DiscordNotifier;
import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistoryUpdater;
import com.moyo.backend.domain.batch.ranking.implement.RankingBatchManager;
import com.moyo.backend.domain.batch.ranking.implement.RankingBatchResult;
import com.moyo.backend.domain.batch.ranking.implement.RankingBatchTaskResult;
import com.moyo.backend.domain.batch.ranking.implement.RankingCalculationProcessor;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchService {

	private final UserReader userReader;
	private final RankingCalculationProcessor rankingCalculationProcessor;
	private final RankingBatchManager rankingBatchManager;
	private final RankingBatchHistoryUpdater rankingBatchHistoryUpdater;

	/**
	 *   모든 User를 한 번에 메모리에 올릴 수는 없음 => OOM 방어
	 *
	 *   <p>
	 *   메모리 사용량 체크하면서 RANKING_BATCH_PAGE_SIZE 조정
	 */
	public void rankingBatch(RankingBatchRequest rankingBatchRequest) {

		List<RankingBatchResult> rankingBatchResults = new ArrayList<>();

		long userIdCursor = 0;
		long lastUserId = rankingBatchRequest.lastUserId();

		while (userIdCursor < lastUserId) {

			List<User> userList = userReader.findAll(userIdCursor, RANKING_BATCH_PAGE_SIZE);
			List<Future<RankingBatchTaskResult>> rankingFutures = rankingCalculationProcessor.calculateRanking(userList);

			rankingBatchResults.add(
				rankingBatchManager.collectResultsAndUpdate(rankingBatchRequest.rankingBatchHistory().getId(), rankingFutures)
			);

			userIdCursor = userList.getLast().getId();
		}

		rankingBatchHistoryUpdater.updateFinalResult(rankingBatchRequest.rankingBatchHistory(), rankingBatchResults);
	}
}
