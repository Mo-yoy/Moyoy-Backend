package com.moyoy.core.domain.user.business;

import com.moyoy.core.domain.ranking.implement.Ranking;
import com.moyoy.core.domain.user.implement.GithubUserProfileMeta;
import com.moyoy.core.domain.user.implement.User;

public record UserProfileResult(
	Long userId,
	String username,
	long yearlyRankPoint,
	String yearlyRankGrade,
	String profileImgUrl,
	int githubFollowerCount,
	int githubFollowingCount
) {

	public static UserProfileResult from(User user, Ranking ranking,GithubUserProfileMeta githubUserProfileMeta) {

		return new UserProfileResult(
			user.getId(),
			user.getUsername(),
			ranking.getYearlyPoint(),
			ranking.getGrade(),
			user.getProfileImgUrl(),
			githubUserProfileMeta.followerCount(),
			githubUserProfileMeta.followingCount()
		);
	}
}
