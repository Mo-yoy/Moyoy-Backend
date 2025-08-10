package com.moyoy.api.github_follow.business;

import com.moyoy.domain.follow.DetectType;

public record GithubFollowDetection(
	DetectType detectType,
	Integer lastGithubUserId,
	int size,
	boolean forceSync) {

	public GithubFollowDetection(
		String detectType,
		Integer lastGithubUserId,
		int pageSize,
		boolean forceSync) {

		this(
			DetectType.fromValue(detectType),
			lastGithubUserId,
			pageSize,
			forceSync);
	}
}
