package com.moyoy.api.user.application.response;

import com.moyoy.infra.database.mysql.query.dto.UserProfileView;

public record UserProfileQueryResult(
	Long userId,
	String username,
	long yearlyRankPoint,
	String yearlyRankGrade,
	String profileImgUrl) {

	public static UserProfileQueryResult from(UserProfileView userProfileView) {
		return new UserProfileQueryResult(
			userProfileView.userId(),
			userProfileView.username(),
			userProfileView.yearlyRankPoint(),
			userProfileView.yearlyRankGrade(),
			userProfileView.profileImgUrl());
	}
}
