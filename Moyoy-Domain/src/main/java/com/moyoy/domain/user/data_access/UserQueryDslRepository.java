package com.moyoy.domain.user.data_access;



import static com.moyoy.domain.user.implement.QUser.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moyoy.domain.ranking.data_access.UserCountAndLastId;
import com.moyoy.domain.user.implement.User;
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
