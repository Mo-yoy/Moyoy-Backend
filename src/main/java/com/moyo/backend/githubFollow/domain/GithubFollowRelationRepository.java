package com.moyo.backend.githubFollow.domain;

public interface GithubFollowRelationRepository {

    GithubFollowRelation findByUserId(Long userId);
}
