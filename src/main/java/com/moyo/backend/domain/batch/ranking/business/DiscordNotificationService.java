package com.moyo.backend.domain.batch.ranking.business;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.implement.DiscordNotifier;
import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistory;
import com.moyo.backend.domain.batch.ranking.implement.RankingBatchNotificationType;
import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistoryReader;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class DiscordNotificationService {

	private final DiscordNotifier discordNotifier;
	private final RankingBatchHistoryReader rankingBatchHistoryReader;

	public void sendNotification(NotificationRequest notificationRequest) {
		String message = formatMessage(
			notificationRequest.type(),
			notificationRequest.rankingBatchHistoryId()
		);
		discordNotifier.sendAlarm(message);
	}

	private String formatMessage(RankingBatchNotificationType notificationType, Long rankingBatchHistoryId) {

		return switch (notificationType) {
			case RANKING_BATCH_START ->
				RankingBatchNotificationType.RANKING_BATCH_START.format(rankingBatchHistoryId);

			case RANKING_BATCH_END -> {
				RankingBatchHistory rankingBatchHistory = rankingBatchHistoryReader.findById(rankingBatchHistoryId);

				Duration duration = Duration.between(rankingBatchHistory.getStartedAt(), rankingBatchHistory.getFinishedAt());
				long hours = duration.toHours();
				long minutes = duration.toMinutes() % 60;
				long seconds = duration.getSeconds() % 60;

				String durationStr = String.format("%02d시간 %02d분 %02d초", hours, minutes, seconds);

				int totalCount = rankingBatchHistory.getTotalCount();
				int successCount = rankingBatchHistory.getSuccessCount();
				int failCount = rankingBatchHistory.getFailCount();
				double successRate = totalCount == 0 ? 0 : (double)successCount / totalCount * 100;

				yield RankingBatchNotificationType.RANKING_BATCH_END.format(
					rankingBatchHistoryId,
					durationStr,
					totalCount,
					successCount,
					failCount,
					successRate
				);
			}
		};
	}
}
