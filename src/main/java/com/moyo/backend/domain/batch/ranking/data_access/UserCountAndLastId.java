package com.moyo.backend.domain.batch.ranking.data_access;

import com.querydsl.core.annotations.QueryProjection;

public record UserCountAndLastId(int userCount, Long lastUserId) {

	@QueryProjection
	public UserCountAndLastId {
	}
}
