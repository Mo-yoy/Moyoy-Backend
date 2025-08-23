package com.moyoy.api.github_follow.presentation.response;

import java.util.List;

import com.moyoy.api.github_follow.application.response.GithubFollowDetectionResult;

import com.moyoy.domain.follow.GithubUser;

import com.moyoy.common.util.TimeSinceFormatter;

public record GithubFollowDetectResponse(
	List<GithubFollowUserDto> userList,
	boolean lastPage,
	int totalUserCount,
	String lastSyncAt) {

	public record GithubFollowUserDto(
		Integer githubUserId,
		String username,
		String profileImgUrl) {
		public static GithubFollowUserDto from(GithubUser user) {
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
			TimeSinceFormatter.formatTimeSince(followDetectionResult.lastSyncAt()));
	}
}
