package com.moyoy.api.user.presentation.response;

import com.moyoy.api.user.application.response.UserSearchResult;

public record UserProfileResponse(
	Long userId,
	String username,
	long rankPoint,
	String rankGrade,
	String profileImgUrl) {

	public static UserProfileResponse from(UserSearchResult userSearchResult) {

		return new UserProfileResponse(
			userSearchResult.userId(),
			userSearchResult.username(),
			userSearchResult.yearlyRankPoint(),
			userSearchResult.yearlyRankGrade(),
			userSearchResult.profileImgUrl());
	}
}
