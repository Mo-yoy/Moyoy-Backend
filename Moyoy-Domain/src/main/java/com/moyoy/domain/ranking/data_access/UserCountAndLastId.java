package com.moyoy.domain.ranking.data_access;

import com.querydsl.core.annotations.QueryProjection;

public record UserCountAndLastId(int userCount, Long lastUserId) {

	@QueryProjection
	public UserCountAndLastId {
	}
}
