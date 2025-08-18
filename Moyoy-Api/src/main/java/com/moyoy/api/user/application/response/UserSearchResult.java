package com.moyoy.api.user.application.response;

import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.user.User;

public record UserSearchResult(
	Long userId,
	String username,
	long yearlyRankPoint,
	String yearlyRankGrade,
	String profileImgUrl) {

	public static UserSearchResult from(User user, Ranking ranking) {

		return new UserSearchResult(
			user.getId(),
			user.getUsername(),
			ranking.getYearlyPoint(),
			ranking.getGrade(),
			user.getProfileImgUrl());
	}
}
