package com.moyo.backend.domain.github_follow.business;

import com.moyo.backend.domain.github_follow.implement.DetectType;

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
