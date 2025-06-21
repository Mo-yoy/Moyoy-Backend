package com.moyo.backend.domain.github_follow.presentation.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import com.moyo.backend.common.util.TimeSinceFormatter;
import com.moyo.backend.domain.github_follow.business.GithubFollowDetectionResult;
import com.moyo.backend.domain.github_follow.implement.GithubUser;

@Getter
@Builder
public class GithubFollowDetectResponse {

	private List<GithubFollowUserDto> userList;
	private boolean lastPage;
	private int totalUserCount;
	private String lastSyncAt;

	@Getter
	@Builder(access = AccessLevel.PRIVATE)
	static class GithubFollowUserDto {
		private Long id;
		private String username;
		private String profileImgUrl;

		public static GithubFollowUserDto from(GithubUser user) {
			return GithubFollowUserDto.builder()
				.id(user.id())
				.username(user.username())
				.profileImgUrl(user.profileImgUrl())
				.build();
		}
	}

	public static GithubFollowDetectResponse from(GithubFollowDetectionResult followDetectionResult) {

		List<GithubFollowUserDto> userDtoList = followDetectionResult.users().getContent().stream()
			.map(GithubFollowUserDto::from)
			.toList();

		return GithubFollowDetectResponse.builder()
			.userList(userDtoList)
			.lastPage(followDetectionResult.users().isLast())
			.totalUserCount(followDetectionResult.totalFollowUserCount())
			.lastSyncAt(TimeSinceFormatter.formatTimeSince(followDetectionResult.lastSyncAt()))
			.build();
	}
}
