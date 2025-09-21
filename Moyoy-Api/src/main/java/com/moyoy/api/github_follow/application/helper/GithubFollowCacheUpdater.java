package com.moyoy.api.github_follow.application.helper;

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

		followCacheStore.findFollowSnapshot(currentUserId)
			.ifPresent(snapshot -> {

				GithubUserProfile targetGithubProfile = targetSupplier.get();
				snapshot.addFollowing(targetGithubProfile);
				followCacheStore.save(currentUserId, snapshot);
			});
	}

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
