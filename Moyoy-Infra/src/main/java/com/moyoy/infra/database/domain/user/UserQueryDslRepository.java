package com.moyoy.infra.database.domain.user;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static com.moyoy.infra.database.domain.user.QUserEntity.userEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public UserCountAndLastId fetchUserCountAndLastId() {

		return jpaQueryFactory
			.select(Projections.constructor(UserCountAndLastId.class,
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
