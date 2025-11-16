package com.moyoy.infra.database.mysql.pr_review;

import static com.moyoy.infra.database.mysql.pr_review.QPrReviewEntity.*;
import static com.moyoy.infra.database.mysql.user.QUserEntity.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.moyoy.infra.database.mysql.pr_review.request.PrReviewQueryConditionData;
import com.moyoy.infra.database.mysql.pr_review.response.PrReviewDetailData;
import com.moyoy.infra.database.mysql.pr_review.response.PrReviewSummaryData;
import com.moyoy.infra.database.mysql.user.QUserEntity;

import com.moyoy.common.page.SliceResult;

@Repository
@RequiredArgsConstructor
public class PrReviewQueryRepositoryImpl implements PrReviewQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public SliceResult<PrReviewSummaryData> findAll(PrReviewQueryConditionData condition) {

		BooleanBuilder builder = new BooleanBuilder();

		if (condition.status() != null) {
			builder.and(prReviewEntity.status.eq(condition.status()));
		}
		if (condition.userId() != null) {
			builder.and(prReviewEntity.userId.eq(condition.userId()));
		}
		if (condition.position() != null) {
			builder.and(prReviewEntity.position.eq(condition.position()));
		}
		if (condition.lastReviewId() != null && condition.lastReviewId() > 0) {
			builder.and(prReviewEntity.id.lt(condition.lastReviewId()));
		}

		OrderSpecifier<?>[] orderSpecifier = toOrderSpecifier(condition.order());

		List<PrReviewSummaryData> results = jpaQueryFactory
			.select(Projections.constructor(
				PrReviewSummaryData.class,
				userEntity.profileImgUrl,
				userEntity.username,
				prReviewEntity.status,
				prReviewEntity.position,
				prReviewEntity.title,
				prReviewEntity.hitCount,
				prReviewEntity.createdAt))
			.from(prReviewEntity)
			.join(userEntity).on(prReviewEntity.userId.eq(userEntity.id))
			.where(builder)
			.orderBy(orderSpecifier)
			.limit(condition.size() + 1)
			.fetch();

		boolean hasNext = results.size() > condition.size();
		if (hasNext) {
			results.removeLast();
		}

		return SliceResult.of(results, hasNext);
	}

	@Override
	public Optional<PrReviewDetailData> findById(Long reviewId) {
		QPrReviewEntity pr = QPrReviewEntity.prReviewEntity;
		QUserEntity user = QUserEntity.userEntity;

		return Optional.ofNullable(jpaQueryFactory
			.select(Projections.constructor(
				PrReviewDetailData.class,
				pr.status,
				user.id,
				user.username,
				user.profileImgUrl,
				pr.title,
				pr.position,
				pr.prUrl,
				pr.content,
				pr.hitCount,
				pr.adopted,
				pr.createdAt,
				pr.closedAt))
			.from(pr)
			.join(user).on(pr.userId.eq(user.id))
			.where(pr.id.eq(reviewId))
			.fetchOne());
	}

	private OrderSpecifier<?>[] toOrderSpecifier(String order) {
		if (order == null || order.isBlank())
			return new OrderSpecifier[] {prReviewEntity.createdAt.desc()};

		return Arrays.stream(order.split(","))
			.map(token -> {
				String[] parts = token.split("-");
				if (parts.length != 2) {
					return prReviewEntity.createdAt.desc();
				}

				String field = parts[0];
				boolean asc = "asc".equalsIgnoreCase(parts[1]);

				return switch (field) {
					case "hitCount" -> asc ? prReviewEntity.hitCount.asc() : prReviewEntity.hitCount.desc();
					case "createdAt" -> asc ? prReviewEntity.createdAt.asc() : prReviewEntity.createdAt.desc();
					default -> prReviewEntity.createdAt.desc();
				};
			})
			.toArray(OrderSpecifier<?>[]::new);
	}
}
