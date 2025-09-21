package com.moyoy.api.github_follow.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;

import com.moyoy.infra.redis.cache.github_follow.GithubUserProfile;

public record GithubFollowDetectResponse(
	List<GithubFollowUserDto> userList,
	boolean lastPage,
	int totalUserCount,
	LocalDateTime lastSyncAt) {

	public record GithubFollowUserDto(
		Integer githubUserId,
		String username,
		String profileImgUrl) {
		public static GithubFollowUserDto from(GithubUserProfile user) {
			return new GithubFollowUserDto(
				user.id(),
				user.username(),
				user.profileImgUrl());
		}
	}

	public static GithubFollowDetectResponse from(GithubFollowDetectionResult followDetectionResult) {

		List<GithubFollowUserDto> userDtoList = followDetectionResult.users()
			.content()
			.stream()
			.map(followUser -> new GithubFollowUserDto(followUser.id(), followUser.username(), followUser.profileImgUrl()))
			.toList();

		return new GithubFollowDetectResponse(
			userDtoList,
			followDetectionResult.users().isLast(),
			followDetectionResult.totalFollowUserCount(),
			followDetectionResult.lastSyncAt());
	}
}
