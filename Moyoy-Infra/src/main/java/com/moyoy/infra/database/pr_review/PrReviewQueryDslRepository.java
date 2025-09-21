package com.moyoy.infra.database.pr_review;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrReviewQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public PrReviewWithAuthorView findByUserId(Long userId) {
        return jpaQueryFactory
                .select(new QPrReviewWithAuthorView(
                        prReviewEntity.id,
                        prReviewEntity.userId,
                        userEntity.githubUserId,
                        userEntity.username,
                        userEntity.profileImgUrl,
                        prReviewEntity.title,
                        prReviewEntity.content,
                        prReviewEntity.prUrl,
                        prReviewEntity.position,
                        prReviewEntity.hitCount,
                        prReviewEntity.status,
                        prReviewEntity.adopted,
                        prReviewEntity.deadline
                ))
                .from(prReviewEntity)
                .join(userEntity).on(prReviewEntity.userId.eq(userEntity.id))
                .where(userEntity.id.eq(userId))
                .fetchOne();
    }
}
