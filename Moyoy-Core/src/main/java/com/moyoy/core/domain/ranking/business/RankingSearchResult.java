package com.moyoy.core.domain.ranking.business;

import java.util.List;

import com.moyoy.core.domain.ranking.implement.Ranking;
import com.moyoy.core.domain.ranking.implement.RankingPeriod;
import com.moyoy.core.domain.ranking.implement.RankingWithUser;

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
