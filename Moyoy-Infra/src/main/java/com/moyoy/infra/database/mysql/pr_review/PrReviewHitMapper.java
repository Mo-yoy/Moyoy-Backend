package com.moyoy.infra.database.mysql.pr_review;

import com.moyoy.domain.pr_review.PrReviewHit;

public class PrReviewHitMapper {

	public static PrReviewHit toModel(PrReviewHitEntity entity) {
		return PrReviewHit.builder()
			.id(entity.getId())
			.prReviewId(entity.getPrReviewId())
			.userId(entity.getUserId())
			.lastIncreasedAt(entity.getLastIncreasedAt())
			.build();
	}

	public static PrReviewHitEntity toEntity(PrReviewHit model) {
		return PrReviewHitEntity.builder()
			.id(model.getId())
			.prReviewId(model.getPrReviewId())
			.userId(model.getUserId())
			.lastIncreasedAt(model.getLastIncreasedAt())
			.build();
	}
}
