package com.moyoy.core.db_access.domain.user;



import static com.moyoy.core.domain.user.implement.QUser.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moyoy.core.domain.user.implement.User;
import com.moyoy.core.db_access.domain.ranking.UserCountAndLastId;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public UserCountAndLastId fetchUserCountAndLastId() {

		return jpaQueryFactory
			.select(Projections.constructor(UserCountAndLastId.class,
				user.count().intValue(),
				user.id.max()))
			.from(user)
			.fetchOne();
	}

	public List<User> findAll(Long lastUserId, int size) {

		return jpaQueryFactory
			.selectFrom(user)
			.where(user.id.gt(lastUserId))
			.limit(size)
			.fetch();
	}
}
