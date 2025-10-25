package com.moyoy.infra.database.mysql.pr_review;

import com.moyoy.domain.pr_review.PrReview;

public class PrReviewMapper {

	public static PrReview toModel(PrReviewEntity entity) {
		return PrReview.builder()
			.id(entity.getId())
			.userId(entity.getUserId())
			.title(entity.getTitle())
			.position(entity.getPosition())
			.prUrl(entity.getPrUrl())
			.content(entity.getContent())
			.hitCount(entity.getHitCount())
			.status(entity.getStatus())
			.adopted(entity.isAdopted())
			.createdAt(entity.getCreatedAt())
			.closedAt(entity.getClosedAt())
			.build();
	}

	public static PrReviewEntity toEntity(PrReview model) {
		return PrReviewEntity.builder()
			.id(model.getId())
			.userId(model.getUserId())
			.title(model.getTitle())
			.position(model.getPosition())
			.prUrl(model.getPrUrl())
			.content(model.getContent())
			.hitCount(model.getHitCount())
			.status(model.getStatus())
			.adopted(model.isAdopted())
			.closedAt(model.getClosedAt())
			.build();
	}
}
