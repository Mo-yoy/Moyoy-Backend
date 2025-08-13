package com.moyoy.api.presentation.v1.domain.user;

import com.moyoy.core.domain.user.business.UserProfileResult;

public record UserProfileResponse(
	Long userId,
	String username,
	long rankPoint,
	String rankGrade,
	String profileImgUrl,
	int followerCount,
	int followingCount) {

	public static UserProfileResponse from(UserProfileResult userProfileResult) {

		return new UserProfileResponse(
			userProfileResult.userId(),
			userProfileResult.username(),
			userProfileResult.yearlyRankPoint(),
			userProfileResult.yearlyRankGrade(),
			userProfileResult.profileImgUrl(),
			userProfileResult.githubFollowerCount(),
			userProfileResult.githubFollowingCount());
	}
}
