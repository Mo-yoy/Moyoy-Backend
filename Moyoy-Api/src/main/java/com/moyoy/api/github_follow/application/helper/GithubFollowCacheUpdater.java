package com.moyoy.api.github_follow.application.helper;

import java.util.Optional;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.infra.redis.cache.github_follow.GithubFollowCacheStore;
import com.moyoy.infra.redis.cache.github_follow.GithubFollowSnapshot;
import com.moyoy.infra.redis.cache.github_follow.GithubUserProfile;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowCacheUpdater {

	private static final int MAX_RETRIES = 3;
	private final GithubFollowCacheStore followCacheStore;

	public void addFollowingToCache(Long currentUserId, Supplier<GithubUserProfile> targetSupplier) {
		updateCacheWithRetry(currentUserId, targetSupplier, GithubFollowSnapshot::addFollowing);
	}

	public void addFollowerToCache(Long targetUserId, Supplier<GithubUserProfile> currentSupplier) {
		updateCacheWithRetry(targetUserId, currentSupplier, GithubFollowSnapshot::addFollower);
	}

	public void removeFollowingFromCache(Long currentUserId, Supplier<GithubUserProfile> targetSupplier) {
		updateCacheWithRetry(currentUserId, targetSupplier, GithubFollowSnapshot::removeFollowing);
	}

	public void removeFollowerFromCache(Long targetUserId, Supplier<GithubUserProfile> currentSupplier) {
		updateCacheWithRetry(targetUserId, currentSupplier, GithubFollowSnapshot::removeFollower);
	}

	private void updateCacheWithRetry(
		Long userId,
		Supplier<GithubUserProfile> profileSupplier,
		CacheUpdateOperation operation) {

		for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
			Optional<GithubFollowSnapshot> snapshotOpt = followCacheStore.findFollowSnapshot(userId);

			if (snapshotOpt.isEmpty()) {
				return;
			}

			GithubFollowSnapshot snapshot = snapshotOpt.get();
			Long currentVersion = snapshot.getVersion();

			GithubUserProfile profile = profileSupplier.get();
			operation.update(snapshot, profile);
			snapshot.versionUp();

			if (followCacheStore.saveWithVersionCheck(userId, snapshot, currentVersion)) {
				return;
			}
		}

		log.warn("캐시 업데이트 최대 재시도 초과 - 캐시 삭제: userId: {}", userId);
		followCacheStore.delete(userId);
	}

	@FunctionalInterface
	private interface CacheUpdateOperation {
		void update(GithubFollowSnapshot snapshot, GithubUserProfile profile);
	}

}
