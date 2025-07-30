package com.moyo.backend.domain.batch.ranking.business;

import static com.moyo.backend.common.constant.MoyoConstants.*;
import static com.moyo.backend.common.util.ThreadUtils.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistory;
import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistoryUpdater;
import com.moyo.backend.domain.batch.ranking.dto.UserRankingBatchSnapshot;
import com.moyo.backend.domain.user.implement.UserReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchPreparationService {

	private final UserReader userReader;
	private final RankingBatchHistoryUpdater rankingBatchHistoryUpdater;

	public RankingBatchPreparationResult prepareRankingBatch() {

		LocalDateTime now = LocalDateTime.now();

		UserRankingBatchSnapshot userRankingBatchSnapshot = userReader.getUserBatchSnapshot();

		RankingBatchHistory rankingBatchHistory = RankingBatchHistory.init(now, currentThreadName(), userRankingBatchSnapshot.userCount());
		rankingBatchHistoryUpdater.update(rankingBatchHistory);

		log(now, userRankingBatchSnapshot);

		return new RankingBatchPreparationResult(userRankingBatchSnapshot, rankingBatchHistory);
	}

	private void log(LocalDateTime now, UserRankingBatchSnapshot userRankingBatchSnapshot){
		log.info("{} 랭킹 배치 작업 시작!", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		log.info("{} 랭킹 배치 작업 | 총 유저 수 : {}", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), userRankingBatchSnapshot.userCount());
		log.info("총 페이지 수: {}", userRankingBatchSnapshot.userCount() / RANKING_BATCH_PAGE_SIZE + 1);
	}
}
