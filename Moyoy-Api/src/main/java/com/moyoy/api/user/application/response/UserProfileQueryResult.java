package com.moyoy.api.user.application.response;

import com.moyoy.infra.database.mysql.query.dto.UserRankingView;

public record UserProfileQueryResult(
	Long userId,
	String username,
	long yearlyRankPoint,
	String yearlyRankGrade,
	String profileImgUrl) {

	public static UserProfileQueryResult from(UserRankingView userRankingView) {
		return new UserProfileQueryResult(
			userRankingView.userId(),
			userRankingView.username(),
			userRankingView.yearlyRankPoint(),
			userRankingView.yearlyRankGrade(),
			userRankingView.profileImgUrl());
	}
}
