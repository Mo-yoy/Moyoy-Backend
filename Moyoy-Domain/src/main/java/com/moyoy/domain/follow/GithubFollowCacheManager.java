package com.moyoy.domain.follow;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = "followRelation")
public class GithubFollowCacheManager {

	private final CacheManager cacheManager;

	@CachePut(key = "#currentUserId", unless = "#result == null")
	public GithubFollowRelation addFollowingToCurrentUser(Long currentUserId, GithubFollowUser targetUser) {

		GithubFollowRelation githubFollowRelation = cacheManager.getCache("followRelation").get(currentUserId, GithubFollowRelation.class);

		if (githubFollowRelation != null)
			githubFollowRelation.addFollowing(targetUser);

		return githubFollowRelation;
	}

	@CachePut(key = "#currentUserId", unless = "#result == null")
	public GithubFollowRelation deleteFollowingToCurrentUser(Long currentUserId, GithubFollowUser targetUser) {

		GithubFollowRelation githubFollowRelation = cacheManager.getCache("followRelation").get(currentUserId, GithubFollowRelation.class);

		if (githubFollowRelation != null)
			githubFollowRelation.removeFollowing(targetUser);

		return githubFollowRelation;
	}

	@CachePut(key = "#targetUserId", unless = "#result == null")
	public GithubFollowRelation addFollowerToTargetUser(Long targetUserId, GithubFollowUser currentUser) {

		GithubFollowRelation githubFollowRelation = cacheManager.getCache("followRelation").get(targetUserId, GithubFollowRelation.class);

		if (githubFollowRelation != null)
			githubFollowRelation.addFollower(currentUser);

		return githubFollowRelation;
	}

	@CachePut(key = "#targetUserId", unless = "#result == null")
	public GithubFollowRelation deleteFollowerToTargetUser(Long targetUserId, GithubFollowUser currentUser) {

		GithubFollowRelation githubFollowRelation = cacheManager.getCache("followRelation").get(targetUserId, GithubFollowRelation.class);

		if (githubFollowRelation != null)
			githubFollowRelation.removeFollower(currentUser);

		return githubFollowRelation;
	}

	@CacheEvict(key = "#userId")
	public void evictCache(Long userId) {

	}
}
