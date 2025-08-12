package com.moyoy.core.db_access.domain.ranking;

import com.querydsl.core.annotations.QueryProjection;

public record UserCountAndLastId(int userCount, Long lastUserId) {

	@QueryProjection
	public UserCountAndLastId {
	}
}
