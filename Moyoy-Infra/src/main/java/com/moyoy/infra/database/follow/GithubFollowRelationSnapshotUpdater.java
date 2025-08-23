package com.moyoy.infra.database.follow;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import com.moyoy.domain.follow.GithubUser;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = "followRelation")
public class GithubFollowRelationSnapshotUpdater {

	private final CacheManager cacheManager;

	@CachePut(key = "#currentUserId", unless = "#result == null")
	public GithubFollowRelationSnapshot addFollowingToCurrentUser(Long currentUserId, GithubUser targetUser) {

		GithubFollowRelationSnapshot followRelationSnapshot
			= cacheManager.getCache("followRelation").get(currentUserId, GithubFollowRelationSnapshot.class);

		if (followRelationSnapshot != null)
			followRelationSnapshot.addFollowing(targetUser);

		return followRelationSnapshot;
	}

	@CachePut(key = "#currentUserId", unless = "#result == null")
	public GithubFollowRelationSnapshot deleteFollowingToCurrentUser(Long currentUserId, GithubUser targetUser) {

		GithubFollowRelationSnapshot followRelation = cacheManager.getCache("followRelation").get(currentUserId, GithubFollowRelationSnapshot.class);

		if (followRelation != null)
			followRelation.removeFollowing(targetUser);

		return followRelation;
	}

	@CachePut(key = "#targetUserId", unless = "#result == null")
	public GithubFollowRelationSnapshot addFollowerToTargetUser(Long targetUserId, GithubUser currentUser) {

		GithubFollowRelationSnapshot followRelation
			= cacheManager.getCache("followRelation").get(targetUserId, GithubFollowRelationSnapshot.class);

		if (followRelation != null)
			followRelation.addFollower(currentUser);

		return followRelation;
	}

	@CachePut(key = "#targetUserId", unless = "#result == null")
	public GithubFollowRelationSnapshot deleteFollowerToTargetUser(Long targetUserId, GithubUser currentUser) {

		GithubFollowRelationSnapshot followRelation = cacheManager.getCache("followRelation").get(targetUserId, GithubFollowRelationSnapshot.class);

		if (followRelation != null)
			followRelation.removeFollower(currentUser);

		return followRelation;
	}

	@CacheEvict(key = "#userId")
	public void evictCache(Long userId) {

	}
}
