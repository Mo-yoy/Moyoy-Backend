package com.moyoy.api.github_follow.application.response;

import java.time.LocalDateTime;

import com.moyoy.infra.redis.cache.github_follow.GithubUserProfile;

import com.moyoy.common.page.SliceResult;

public record GithubFollowDetectionResult(

	SliceResult<GithubUserProfile> users,
	LocalDateTime lastSyncAt,
	int totalFollowUserCount) {

	public static GithubFollowDetectionResult of(SliceResult<GithubUserProfile> users, LocalDateTime lastSyncAt, int totalFollowUserCount) {
		return new GithubFollowDetectionResult(users, lastSyncAt, totalFollowUserCount);
	}

}
