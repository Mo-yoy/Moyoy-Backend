package com.moyo.backend.githubFollow.domain;

public interface GithubFollowRelationClient {

    GithubFollowRelation load(Long userId, String username, String accessToken);

    void follow(Long currentUserId, Long targetUserId, String accessToken);

    void unfollow(Long currentUserId, Long targetUserId, String accessToken);
}
