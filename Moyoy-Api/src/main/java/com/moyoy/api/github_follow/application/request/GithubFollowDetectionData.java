package com.moyoy.api.github_follow.application.request;

import com.moyoy.domain.follow.DetectType;

public record GithubFollowDetectionData(
	DetectType detectType,
	Integer lastGithubUserId,
	int size) {

	public static GithubFollowDetectionData of(String detectType, int lastGithubUserId, int size) {
		return new GithubFollowDetectionData(DetectType.fromValue(detectType), lastGithubUserId, size);
	}
}
