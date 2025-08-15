package com.moyoy.infra.database.user;

import com.querydsl.core.annotations.QueryProjection;

public record UserEntityMetaDto(int userCount, Long lastUserId) {

	@QueryProjection
	public UserEntityMetaDto {
	}
}
