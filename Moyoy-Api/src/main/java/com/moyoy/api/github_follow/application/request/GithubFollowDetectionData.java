package com.moyoy.api.github_follow.application.request;

import com.moyoy.domain.follow.DetectType;

public record GithubFollowDetectionData(
	DetectType detectType,
	Integer lastGithubUserId,
	int size,
	boolean forceSync) {

	public static GithubFollowDetectionData of(String detectType, int lastGithubUserId, int size, boolean forceSync) {
		return new GithubFollowDetectionData(DetectType.fromValue(detectType), lastGithubUserId, size, forceSync);
	}
}
