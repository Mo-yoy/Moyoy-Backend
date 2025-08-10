package com.moyoy.api.github_follow.presentation;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import com.moyoy.api.github_follow.business.GithubFollowDetectionResult;
import com.moyoy.common.util.TimeSinceFormatter;
import com.moyoy.domain.follow.GithubFollowUser;

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
		private Integer githubUserId;
		private String username;
		private String profileImgUrl;

		public static GithubFollowUserDto from(GithubFollowUser user) {
			return GithubFollowUserDto.builder()
				.githubUserId(user.id())
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
