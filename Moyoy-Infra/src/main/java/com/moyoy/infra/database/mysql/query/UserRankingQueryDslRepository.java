package com.moyoy.infra.database.mysql.query;

import static com.moyoy.infra.database.mysql.ranking.QRankingEntity.*;
import static com.moyoy.infra.database.mysql.user.QUserEntity.*;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.moyoy.domain.ranking.RankingPeriod;

import com.moyoy.infra.database.mysql.query.dto.UserProfileView;
import com.moyoy.infra.database.mysql.query.dto.UserRankingView;

import com.moyoy.common.page.PageData;
import com.moyoy.common.page.SliceResult;

@Repository
@RequiredArgsConstructor
class UserRankingQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Optional<UserProfileView> findUserRankingView(Long userId) {

		UserProfileView result = jpaQueryFactory
			.select(Projections.constructor(UserProfileView.class,
				userEntity.id,
				userEntity.username,
				rankingEntity.yearlyPoint,
				rankingEntity.grade,
				userEntity.profileImgUrl))
			.from(userEntity)
			.join(rankingEntity)
			.on(userEntity.id.eq(rankingEntity.userId))
			.where(userEntity.id.eq(userId))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	public SliceResult<UserRankingView> findAll(RankingPeriod period, PageData pageData) {

		NumberPath<Long> rankPoint = resolveRankPoint(period);

		List<UserRankingView> result = jpaQueryFactory
			.select(Projections.constructor(UserRankingView.class,
				userEntity.id,
				userEntity.profileImgUrl,
				userEntity.username,
				rankPoint))
			.from(userEntity)
			.join(rankingEntity)
			.on(userEntity.id.eq(rankingEntity.userId))
			.offset(pageData.offset())
			.limit(pageData.size() + 1)
			.orderBy(rankPoint.desc(), userEntity.id.asc())
			.fetch();

		boolean hasNext = result.size() > pageData.size();
		if (hasNext)
			result.removeLast();

		return SliceResult.of(result, hasNext);
	}

	public NumberPath<Long> resolveRankPoint(RankingPeriod period) {
		return switch (period) {
			case RankingPeriod.WEEK -> rankingEntity.weeklyPoint;
			case RankingPeriod.MONTH -> rankingEntity.monthlyPoint;
			case RankingPeriod.YEAR -> rankingEntity.yearlyPoint;
		};
	}
}
