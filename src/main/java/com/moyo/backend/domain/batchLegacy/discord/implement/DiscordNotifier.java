package com.moyo.backend.domain.batchLegacy.discord.implement;

import java.time.Duration;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batchLegacy.ranking.data_access.DiscordRestClientImpl;
import com.moyo.backend.domain.batchLegacy.ranking.implement.RankingBatchHistory;
import com.moyo.backend.domain.batchLegacy.ranking.implement.RankingBatchHistoryReader;

@Component
@RequiredArgsConstructor
public class DiscordNotifier {

	private final DiscordRestClientImpl discordRestClientImpl;
	private final RankingBatchHistoryReader rankingBatchHistoryReader;

	public void sendNotification(NotificationRequest notificationRequest) {
		String message = formatMessage(
			notificationRequest.type(),
			notificationRequest.rankingBatchHistoryId());
		discordRestClientImpl.sendAlarm(message);
	}

	private String formatMessage(NotificationType notificationType, Long rankingBatchHistoryId) {

		return switch (notificationType) {
			case RANKING_BATCH_START ->
				NotificationType.RANKING_BATCH_START.format(rankingBatchHistoryId);

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

				yield NotificationType.RANKING_BATCH_END.format(
					rankingBatchHistoryId,
					durationStr,
					totalCount,
					successCount,
					failCount,
					successRate);
			}
		};
	}
}
