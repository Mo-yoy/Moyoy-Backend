package com.moyoy.core.db_access.domain.ranking;

import static com.moyoy.core.domain.ranking.implement.QRanking.*;
import static com.moyoy.core.domain.user.implement.QUser.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.moyoy.core.domain.ranking.implement.Ranking;
import com.moyoy.core.domain.ranking.implement.RankingPeriod;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankingQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Slice<Ranking> findAll(RankingPeriod duration, Pageable pageable) {

		OrderSpecifier<?> orderCondition = switch (duration) {
			case RankingPeriod.WEEK -> ranking.weeklyPoint.desc();
			case RankingPeriod.MONTH -> ranking.monthlyPoint.desc();
			case RankingPeriod.YEAR -> ranking.yearlyPoint.desc();
		};

		List<Ranking> rankings = jpaQueryFactory
			.selectFrom(ranking)
			.orderBy(orderCondition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = rankings.size() > pageable.getPageSize();
		if (hasNext)
			rankings.removeLast();

		return new SliceImpl<>(rankings, pageable, hasNext);
	}

	public Slice<Ranking> findByUserIds(List<Integer> followingUserIds, RankingPeriod rankingPeriod, Pageable pageable) {

		OrderSpecifier<?> orderCondition = switch (rankingPeriod) {
			case RankingPeriod.WEEK -> ranking.weeklyPoint.desc();
			case RankingPeriod.MONTH -> ranking.monthlyPoint.desc();
			case RankingPeriod.YEAR -> ranking.yearlyPoint.desc();
		};

		List<Ranking> rankings = jpaQueryFactory
			.selectFrom(ranking)
			.where(ranking.userId.in(
				JPAExpressions
					.select(user.id)
					.from(user)
					.where(user.githubUserId.in(followingUserIds))))
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
