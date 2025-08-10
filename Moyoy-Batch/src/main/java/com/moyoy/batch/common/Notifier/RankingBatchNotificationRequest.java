package com.moyoy.batch.common.Notifier;

public record RankingBatchNotificationRequest(
	RankingBatchType type,
	Long batchId) {

	public static RankingBatchNotificationRequest of(RankingBatchType type, Long batchId) {
		return new RankingBatchNotificationRequest(type, batchId);
	}
}
