package com.moyo.backend.domain.batch.ranking.business;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchManager;
import com.moyo.backend.domain.batch.ranking.implement.RankingCalculationProcessor;
import com.moyo.backend.domain.github_ranking.implement.Ranking;
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

	/**
	 *   모든 User를 한 번에 메모리에 올릴 수는 없음 => OOM 방어
	 *
	 *   <p>
	 *   메모리 사용량 체크하면서 RANKING_BATCH_PAGE_SIZE 조정
	 */
	public void rankingBatch(RankingBatchRequest rankingBatchRequest) {

		long userIdCursor = 0;
		long lastUserId = rankingBatchRequest.lastUserId();

		while (userIdCursor < lastUserId) {

			List<User> userList = userReader.findAll(userIdCursor, RANKING_BATCH_PAGE_SIZE);
			List<Future<Ranking>> rankingFutures = rankingCalculationProcessor.calculateRanking(userList);

			rankingBatchManager.updateRankings(rankingBatchRequest.rankingBatchHistoryId(), rankingFutures);

			userIdCursor = userList.getLast().getId();
		}
	}
}
