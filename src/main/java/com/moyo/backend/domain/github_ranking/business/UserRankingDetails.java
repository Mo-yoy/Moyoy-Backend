package com.moyo.backend.domain.github_ranking.business;

import com.moyo.backend.domain.github_ranking.implement.RankingDuration;
import com.moyo.backend.domain.user.implement.User;

public record UserRankingDetails(
	String profileImageUrl,
	String username,
	long rankPoint) {

	public static UserRankingDetails from(User user, RankingDuration duration) {

		return switch (duration) {
			case WEEK ->
				new UserRankingDetails(user.getProfileImgUrl(), user.getUsername(), user.getRanking().getWeeklyPoint());
			case MONTH ->
				new UserRankingDetails(user.getProfileImgUrl(), user.getUsername(), user.getRanking().getMonthlyPoint());
			case YEAR ->
				new UserRankingDetails(user.getProfileImgUrl(), user.getUsername(), user.getRanking().getYearlyPoint());
		};
	}
}
