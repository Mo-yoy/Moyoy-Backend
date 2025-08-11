package com.moyoy.api.presentation.v1.domain.follow;

import java.util.List;

import com.moyoy.common.util.TimeSinceFormatter;
import com.moyoy.core.domain.follow.business.GithubFollowDetectionResult;
import com.moyoy.core.domain.follow.implement.GithubFollowUser;

public record GithubFollowDetectResponse(
	List<GithubFollowUserDto> userList,
	boolean lastPage,
	int totalUserCount,
	String lastSyncAt) {

	public record GithubFollowUserDto(
		Integer githubUserId,
		String username,
		String profileImgUrl) {
		public static GithubFollowUserDto from(GithubFollowUser user) {
			return new GithubFollowUserDto(
				user.id(),
				user.username(),
				user.profileImgUrl());
		}
	}

	public static GithubFollowDetectResponse from(GithubFollowDetectionResult followDetectionResult) {
		List<GithubFollowUserDto> userDtoList = followDetectionResult.users()
			.getContent()
			.stream()
			.map(GithubFollowUserDto::from)
			.toList();

		return new GithubFollowDetectResponse(
			userDtoList,
			followDetectionResult.users().isLast(),
			followDetectionResult.totalFollowUserCount(),
			TimeSinceFormatter.formatTimeSince(followDetectionResult.lastSyncAt()));
	}
}
