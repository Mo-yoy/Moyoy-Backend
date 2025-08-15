package com.moyoy.infra.database.ranking;

import static com.moyoy.infra.database.ranking.QRankingEntity.*;
import static com.moyoy.infra.database.user.QUserEntity.*;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.moyoy.domain.ranking.RankingPeriod;

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
}
