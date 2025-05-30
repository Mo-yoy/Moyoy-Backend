package com.moyo.backend.githubFollow.application;

import com.moyo.backend.githubFollow.domain.GithubFollowRelation;

import java.util.Optional;

public interface FollowRelationRepository {
    Optional<GithubFollowRelation> findByUserId(Long currentUserId);

    void save(Long currentUserId, GithubFollowRelation githubFollowRelation);

    void clearFollowCache(Long currentUserId);

    void update(Long currentUserId, GithubFollowRelation githubFollowRelation);

}
