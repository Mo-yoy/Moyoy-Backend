package com.moyo.backend.domain.batch.ranking.business;



import com.moyo.backend.domain.batch.ranking.implement.RankingBatchNotificationType;

public record NotificationRequest(
	RankingBatchNotificationType type,
	Long rankingBatchHistoryId) {

	public static NotificationRequest of(RankingBatchNotificationType type, Long rankingBatchHistoryId) {
		return new NotificationRequest(type, rankingBatchHistoryId);
	}

}
