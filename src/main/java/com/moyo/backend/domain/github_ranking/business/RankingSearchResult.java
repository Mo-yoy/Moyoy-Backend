package com.moyo.backend.domain.github_ranking.business;

import java.util.List;

import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingPeriod;
import com.moyo.backend.domain.github_ranking.implement.RankingWithUser;

public record RankingSearchResult(
	List<RankingWithUser> rankingWithUsers,
	boolean isLast) {

	public long getPointByDuration(Ranking ranking, String period) {
		return switch (RankingPeriod.valueOf(period.toUpperCase())) {
			case WEEK -> ranking.getWeeklyPoint();
			case MONTH -> ranking.getMonthlyPoint();
			case YEAR -> ranking.getYearlyPoint();
		};
	}
}
