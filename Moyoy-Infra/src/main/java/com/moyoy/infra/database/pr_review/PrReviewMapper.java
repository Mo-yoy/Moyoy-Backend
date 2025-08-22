package com.moyoy.infra.database.pr_review;

import com.moyoy.domain.pr_review.Author;
import com.moyoy.domain.pr_review.PrReview;
import com.moyoy.domain.user.User;

public class PrReviewMapper {

    public static PrReview toModel(PrReviewEntity entity, Author author) {
        if (entity == null) return null;
        return PrReview.builder()
                .id(entity.getId())
                .author(author)
                .title(entity.getTitle())
                .content(entity.getContent())
                .prUrl(entity.getPrUrl())
                .position(entity.getPosition())
                .hitCount(entity.getHitCount())
                .status(entity.getStatus())
                .adopted(entity.isAdopted())
                .deadline(entity.getDeadline())
                .build();
    }

    public static PrReviewEntity toEntity(PrReview model) {
        return PrReviewEntity.builder()
                .id(model.getId())
                .userId(model.getAuthor().id())
                .title(model.getTitle())
                .content(model.getContent())
                .prUrl(model.getPrUrl())
                .position(model.getPosition())
                .hitCount(model.getHitCount())
                .status(model.getStatus())
                .adopted(model.isAdopted())
                .deadline(model.getDeadline())
                .build();
    }

    public static Author toAuthor(User user) {
        return new Author(user.getId(), user.getGithubUserId(), user.getUsername(), user.getProfileImgUrl());
    }
}
