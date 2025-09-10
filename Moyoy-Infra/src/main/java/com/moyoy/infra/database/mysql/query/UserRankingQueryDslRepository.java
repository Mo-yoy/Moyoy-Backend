package com.moyoy.infra.database.mysql.query;

import static com.moyoy.infra.database.mysql.ranking.QRankingEntity.*;
import static com.moyoy.infra.database.mysql.user.QUserEntity.*;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
@RequiredArgsConstructor
public class UserRankingQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<UserRankingView> findUserRankingView(Long userId) {
		UserRankingView result = jpaQueryFactory
			.select(Projections.constructor(UserRankingView.class,
				userEntity.id,
				userEntity.username,
				rankingEntity.yearlyPoint,
				rankingEntity.grade,
				userEntity.profileImgUrl))
			.from(userEntity)
			.join(rankingEntity).on(userEntity.id.eq(rankingEntity.userId))
			.where(userEntity.id.eq(userId))
			.fetchOne();

		return Optional.ofNullable(result);
	}
}
