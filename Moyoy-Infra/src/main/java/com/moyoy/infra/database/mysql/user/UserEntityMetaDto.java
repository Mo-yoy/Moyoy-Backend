package com.moyoy.infra.database.mysql.user;

import com.querydsl.core.annotations.QueryProjection;

public record UserEntityMetaDto(int userCount, Long lastUserId) {

	@QueryProjection
	public UserEntityMetaDto {
	}
}
