package com.moyoy.api.user.application.response;

import com.moyoy.api.user.application.request.UserGithubFollowStats;
import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.user.User;

public record UserProfileResult(
	Long userId,
	String username,
	long yearlyRankPoint,
	String yearlyRankGrade,
	String profileImgUrl,
	int githubFollowerCount,
	int githubFollowingCount) {

	public static UserProfileResult from(User user, Ranking ranking, UserGithubFollowStats userGithubFollowStats) {

		return new UserProfileResult(
			user.getId(),
			user.getUsername(),
			ranking.getYearlyPoint(),
			ranking.getGrade(),
			user.getProfileImgUrl(),
			userGithubFollowStats.followerCount(),
			userGithubFollowStats.followingCount());
	}
}
