package com.moyoy.infra.database.domain.user;

import com.querydsl.core.annotations.QueryProjection;

public record UserCountAndLastId(int userCount, Long lastUserId) {

	@QueryProjection
	public UserCountAndLastId {
	}
}
