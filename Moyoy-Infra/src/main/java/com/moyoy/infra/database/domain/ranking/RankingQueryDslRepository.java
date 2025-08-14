package com.moyoy.infra.database.domain.ranking;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.moyoy.common.enums.RankingPeriod;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static com.moyoy.infra.database.domain.ranking.QRankingEntity.rankingEntity;
import static com.moyoy.infra.database.domain.user.QUserEntity.userEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankingQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Slice<RankingEntity> findAll(RankingPeriod duration, Pageable pageable) {

		OrderSpecifier<?> orderCondition = switch (duration) {
			case RankingPeriod.WEEK -> rankingEntity.weeklyPoint.desc();
			case RankingPeriod.MONTH -> rankingEntity.monthlyPoint.desc();
			case RankingPeriod.YEAR -> rankingEntity.yearlyPoint.desc();
		};

		List<RankingEntity> rankings = jpaQueryFactory
			.selectFrom(rankingEntity)
			.orderBy(orderCondition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = rankings.size() > pageable.getPageSize();
		if (hasNext)
			rankings.removeLast();

		return new SliceImpl<>(rankings, pageable, hasNext);
	}

	public Slice<RankingEntity> findByUserIds(List<Integer> followingUserIds, RankingPeriod rankingPeriod, Pageable pageable) {

		OrderSpecifier<?> orderCondition = switch (rankingPeriod) {
			case RankingPeriod.WEEK -> rankingEntity.weeklyPoint.desc();
			case RankingPeriod.MONTH -> rankingEntity.monthlyPoint.desc();
			case RankingPeriod.YEAR -> rankingEntity.yearlyPoint.desc();
		};

		List<RankingEntity> rankings = jpaQueryFactory
			.selectFrom(rankingEntity)
			.where(rankingEntity.userId.in(
				JPAExpressions
					.select(userEntity.id)
					.from(userEntity)
					.where(userEntity.githubUserId.in(followingUserIds))))
			.orderBy(orderCondition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = rankings.size() > pageable.getPageSize();
		if (hasNext)
			rankings.removeLast();

		return new SliceImpl<>(rankings, pageable, hasNext);
	}
}
