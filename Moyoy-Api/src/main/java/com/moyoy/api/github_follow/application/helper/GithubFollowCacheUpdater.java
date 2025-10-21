package com.moyoy.api.github_follow.application.helper;

import com.moyoy.infra.redis.cache.github_follow.GithubFollowSnapshot;
import java.util.Optional;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.infra.redis.cache.github_follow.GithubFollowCacheStore;
import com.moyoy.infra.redis.cache.github_follow.GithubUserProfile;

@Component
@RequiredArgsConstructor
public class GithubFollowCacheUpdater {

	private final GithubFollowCacheStore followCacheStore;

	public void addFollowingToCache(Long currentUserId, Supplier<GithubUserProfile> targetSupplier) {
		int maxRetries = 3;

		for (int attempt = 0; attempt < maxRetries; attempt++) {
			Optional<GithubFollowSnapshot> optionalSnapshot = followCacheStore.findFollowSnapshot(currentUserId);
			if (optionalSnapshot.isEmpty()) {
				return;
			}

			GithubFollowSnapshot snapshot = optionalSnapshot.get();
			Long currentVersion = snapshot.getVersion();

			GithubUserProfile targetGithubProfile = targetSupplier.get();
			snapshot.addFollowing(targetGithubProfile);
			snapshot.versionUp();

			if (followCacheStore.saveWithVersionCheck(currentUserId, snapshot, currentVersion)) {
				return;
			}
		}

		followCacheStore.delete(currentUserId);
	}


//	public void addFollowingToCache(Long currentUserId, Supplier<GithubUserProfile> targetSupplier) {
//
//		followCacheStore.findFollowSnapshot(currentUserId)
//			.ifPresent(snapshot -> {
//
//				GithubUserProfile targetGithubProfile = targetSupplier.get();
//				snapshot.addFollowing(targetGithubProfile);
//				followCacheStore.save(currentUserId, snapshot);
//			});
//	}

	public void addFollowerToCache(Long targetUserId, Supplier<GithubUserProfile> currentSupplier) {

		followCacheStore.findFollowSnapshot(targetUserId)
			.ifPresent(snapshot -> {

				GithubUserProfile currentGithubProfile = currentSupplier.get();
				snapshot.addFollower(currentGithubProfile);
				followCacheStore.save(targetUserId, snapshot);
			});
	}

	public void removeFollowingFromCache(Long currentUserId, Supplier<GithubUserProfile> targetSupplier) {

		followCacheStore.findFollowSnapshot(currentUserId)
			.ifPresent(snapshot -> {

				GithubUserProfile targetGithubUserProfile = targetSupplier.get();
				snapshot.removeFollowing(targetGithubUserProfile);
				followCacheStore.save(currentUserId, snapshot);
			});
	}

	public void removeFollowerFromCache(Long targetUserId, Supplier<GithubUserProfile> currentSupplier) {

		followCacheStore.findFollowSnapshot(targetUserId)
			.ifPresent(snapshot -> {

				GithubUserProfile currentGithubProfile = currentSupplier.get();
				snapshot.removeFollower(currentGithubProfile);
				followCacheStore.save(targetUserId, snapshot);
			});
	}
}
