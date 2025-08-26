package com.moyoy.infra.database.mysql.user;

import static com.moyoy.infra.database.mysql.user.QUserEntity.userEntity;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public UserEntityMetaDto fetchUserCountAndLastId() {

		return jpaQueryFactory
			.select(Projections.constructor(UserEntityMetaDto.class,
				userEntity.count().intValue(),
				userEntity.id.max()))
			.from(userEntity)
			.fetchOne();
	}

	public List<UserEntity> findAll(Long lastUserId, int size) {

		return jpaQueryFactory
			.selectFrom(userEntity)
			.where(userEntity.id.gt(lastUserId))
			.limit(size)
			.fetch();
	}
}
