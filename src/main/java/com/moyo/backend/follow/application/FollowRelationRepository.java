package com.moyo.backend.follow.application;

import com.moyo.backend.follow.domain.FollowRelation;

import java.util.Optional;

public interface FollowRelationRepository {
    Optional<FollowRelation> findByUserId(Long currentUserId);

    void save(Long currentUserId, FollowRelation followRelation);

    void clearFollowCache(Long currentUserId);

    void update(Long currentUserId, FollowRelation followRelation);

}
