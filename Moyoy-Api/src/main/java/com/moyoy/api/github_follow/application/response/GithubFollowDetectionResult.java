package com.moyoy.api.github_follow.application.response;

import java.time.LocalDateTime;

import com.moyoy.domain.follow.GithubUser;
import com.moyoy.domain.support.page.SliceResult;

public record GithubFollowDetectionResult(

	SliceResult<GithubUser> users,
	LocalDateTime lastSyncAt,
	int totalFollowUserCount) {

	public static GithubFollowDetectionResult of(SliceResult<GithubUser> users, LocalDateTime lastSyncAt, int totalFollowUserCount) {
		return new GithubFollowDetectionResult(users, lastSyncAt, totalFollowUserCount);
	}

}
