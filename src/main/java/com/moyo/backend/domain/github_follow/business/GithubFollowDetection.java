package com.moyo.backend.domain.github_follow.business;

import com.moyo.backend.domain.github_follow.implement.DetectType;

public record GithubFollowDetection(
	DetectType detectType,
	Long lastUserId,
	int size,
	boolean forceSync) {

	public GithubFollowDetection(
		String detectType,
		Long lastUserId,
		int pageSize,
		boolean forceSync) {

		this(
			DetectType.fromValue(detectType),
			lastUserId,
			pageSize,
			forceSync);
	}
}
