package com.moyo.backend.githubfollow.service;

import com.moyo.backend.githubfollow.model.FollowUser;

import java.time.LocalDateTime;
import java.util.List;

public interface GithubFollowRepository {
    boolean existByUserId(Long currentUserId);

    void saveFollowCreatedAt(Long currentUserId, LocalDateTime now);

    void saveFollowingList(Long currentUserId, List<FollowUser> followings);

    void saveFollowerList(Long currentUserId, List<FollowUser> followers);

    List<FollowUser> getFollowingList(Long currentUserId);

    List<FollowUser> getFollowerList(Long currentUserId);

    void deleteFollowCache(Long userId);

    void saveFollowing(Long currentUserId, FollowUser followUser);

    void saveFollower(Long targetUserId, FollowUser currentUser);

    void deleteFollowingUser(Long currentUserId, Long targetUserId);

    void deleteFollowerUser(Long targetUserId, Long currentUserId);
}
