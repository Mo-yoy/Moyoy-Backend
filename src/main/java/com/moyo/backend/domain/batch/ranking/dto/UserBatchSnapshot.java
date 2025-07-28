package com.moyo.backend.domain.batch.ranking.dto;

public record UserBatchSnapshot(
	int userCount,
	Long lastUserId
) {

	public static UserBatchSnapshot from(UserCountAndLastId userCountAndLastId) {
		return new UserBatchSnapshot(userCountAndLastId.userCount(), userCountAndLastId.lastUserId());
	}
}
