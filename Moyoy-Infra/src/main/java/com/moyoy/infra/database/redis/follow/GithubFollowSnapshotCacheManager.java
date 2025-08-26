package com.moyoy.infra.database.redis.follow;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import com.moyoy.domain.follow.GithubFollowSnapshot;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowSnapshotCacheManager {

	private final CacheManager cacheManager;

	public Optional<GithubFollowSnapshot> findFollowSnapshot(Long userId) {

		GithubFollowSnapshot githubFollowSnapshot = cacheManager.getCache("followSnapshot").get(userId, GithubFollowSnapshot.class);
		return Optional.ofNullable(githubFollowSnapshot);
	}

	@CachePut(cacheNames = "followSnapshot", key = "#userId")
	public GithubFollowSnapshot save(Long userId, GithubFollowSnapshot githubFollowRelationSnapshot) {
		return githubFollowRelationSnapshot;
	}

}
