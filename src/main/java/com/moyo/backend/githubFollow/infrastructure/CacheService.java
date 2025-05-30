package com.moyo.backend.githubFollow.infrastructure;

import com.moyo.backend.githubFollow.domain.GithubFollowRelation;
import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    @CachePut(value = "followRelation", key = "#currentUserId")
    public GithubFollowRelation addFollowingToCurrentUser(Long currentUserId, GithubFollowUser targetUser) {

        GithubFollowRelation githubFollowRelation = cacheManager.getCache("followRelation").get(currentUserId, GithubFollowRelation.class);

        if (githubFollowRelation != null) githubFollowRelation.addFollowing(targetUser);

        return githubFollowRelation;
    }

    @CachePut(value = "followRelation", key = "#currentUserId")
    public GithubFollowRelation deleteFollowingToCurrentUser(Long currentUserId, Long targetUserId) {

        GithubFollowRelation githubFollowRelation = cacheManager.getCache("followRelation").get(currentUserId, GithubFollowRelation.class);

        if (githubFollowRelation != null) githubFollowRelation.removeFollowing(targetUserId);

        return githubFollowRelation;
    }

    @CachePut(value = "followRelation", key = "#targetUserId")
    public GithubFollowRelation addFollowerToTargetUser(Long targetUserId, GithubFollowUser currentUser) {

        GithubFollowRelation githubFollowRelation = cacheManager.getCache("followRelation").get(targetUserId, GithubFollowRelation.class);

        if (githubFollowRelation != null) githubFollowRelation.addFollower(currentUser);

        return githubFollowRelation;
    }

    @CachePut(value = "followRelation", key = "#targetUserId")
    public GithubFollowRelation deleteFollowerToTargetUser(Long currentUserId, Long targetUserId) {

        GithubFollowRelation githubFollowRelation = cacheManager.getCache("followRelation").get(targetUserId, GithubFollowRelation.class);

        if (githubFollowRelation != null) githubFollowRelation.removeFollower(currentUserId);

        return githubFollowRelation;
    }
}
