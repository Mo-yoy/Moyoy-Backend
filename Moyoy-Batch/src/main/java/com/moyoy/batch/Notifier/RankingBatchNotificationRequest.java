package com.moyoy.batch.Notifier;

public record RankingBatchNotificationRequest(
	RankingBatchType type,
	Long batchId) {

	public static RankingBatchNotificationRequest of(RankingBatchType type, Long batchId) {
		return new RankingBatchNotificationRequest(type, batchId);
	}
}
