package com.moyo.backend.domain.batchLegacy.ranking.implement;

import static com.moyo.backend.common.constant.MoyoConstants.*;
import static com.moyo.backend.common.util.ThreadUtils.*;
import static com.moyo.backend.domain.batchLegacy.discord.implement.NotificationType.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batchLegacy.discord.implement.DiscordNotifier;
import com.moyo.backend.domain.batchLegacy.discord.implement.NotificationRequest;
import com.moyo.backend.domain.batchLegacy.discord.implement.NotificationType;
import com.moyo.backend.domain.batchLegacy.ranking.business.RankingBatchPreparationResult;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchManager {

	private final UserReader userReader;
	private final RankingBatchHistoryUpdater rankingBatchHistoryUpdater;
	private final DiscordNotifier discordNotifier;
	private final RankingCalculationProcessor rankingCalculationProcessor;
	private final RankingBatchResultWriter rankingBatchResultWriter;

	public RankingBatchPreparationResult prepareRankingBatch() {

		LocalDateTime now = LocalDateTime.now();

		UserRankingBatchSnapshot userRankingBatchSnapshot = userReader.getUserBatchSnapshot();

		RankingBatchHistory rankingBatchHistory = RankingBatchHistory.init(now, currentThreadName(), userRankingBatchSnapshot.userCount());
		rankingBatchHistoryUpdater.update(rankingBatchHistory);

		log(now, userRankingBatchSnapshot);
		sendNotification(RANKING_BATCH_START, rankingBatchHistory.getId());

		return new RankingBatchPreparationResult(userRankingBatchSnapshot, rankingBatchHistory);
	}

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
				rankingBatchResultWriter.collectResultsAndUpdate(rankingBatchRequest.rankingBatchHistory().getId(), rankingFutures)
			);

			userIdCursor = userList.getLast().getId();
		}

		rankingBatchHistoryUpdater.updateFinalResult(rankingBatchRequest.rankingBatchHistory(), rankingBatchResults);

		sendNotification(RANKING_BATCH_END, rankingBatchRequest.rankingBatchHistory().getId());
	}

	private void log(LocalDateTime now, UserRankingBatchSnapshot userRankingBatchSnapshot) {
		log.info("{} 랭킹 배치 작업 시작!", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		log.info("{} 랭킹 배치 작업 | 총 유저 수 : {}", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), userRankingBatchSnapshot.userCount());
		log.info("총 페이지 수: {}", userRankingBatchSnapshot.userCount() / RANKING_BATCH_PAGE_SIZE + 1);
	}

	private void sendNotification(NotificationType notificationType, Long RankingBatchHistoryId){

		discordNotifier.sendNotification(NotificationRequest.of(notificationType, RankingBatchHistoryId));
	}


}
