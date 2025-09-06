package com.moyoy.api.user.presentation.response;

import com.moyoy.api.user.application.response.UserProfileQueryResult;

public record UserProfileResponse(
	Long userId,
	String username,
	long rankPoint,
	String rankGrade,
	String profileImgUrl) {

	public static UserProfileResponse from(UserProfileQueryResult userProfileQueryResult) {

		return new UserProfileResponse(
			userProfileQueryResult.userId(),
			userProfileQueryResult.username(),
			userProfileQueryResult.yearlyRankPoint(),
			userProfileQueryResult.yearlyRankGrade(),
			userProfileQueryResult.profileImgUrl());
	}
}
