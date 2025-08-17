package com.moyoy.api.user.presentation.response;

import com.moyoy.api.user.application.response.UserProfileResult;

public record UserProfileResponse(
	Long userId,
	String username,
	long rankPoint,
	String rankGrade,
	String profileImgUrl) {

	public static UserProfileResponse from(UserProfileResult userProfileResult) {

		return new UserProfileResponse(
			userProfileResult.userId(),
			userProfileResult.username(),
			userProfileResult.yearlyRankPoint(),
			userProfileResult.yearlyRankGrade(),
			userProfileResult.profileImgUrl()
		);
	}
}
