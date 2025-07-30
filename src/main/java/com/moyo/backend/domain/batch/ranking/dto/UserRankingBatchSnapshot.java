package com.moyo.backend.domain.batch.ranking.dto;

public record UserRankingBatchSnapshot(
	int userCount,
	Long lastUserId) {

	public static UserRankingBatchSnapshot from(UserCountAndLastId userCountAndLastId) {
		return new UserRankingBatchSnapshot(userCountAndLastId.userCount(), userCountAndLastId.lastUserId());
	}
}
