package com.moyoy.api.ranking.application.response;

import java.util.List;

import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingPeriod;

public record RankingSearchResult(
	List<RankingUserView> rankingUserViews,
	boolean isLast) {

	public long getPointByDuration(Ranking ranking, String period) {
		return switch (RankingPeriod.valueOf(period.toUpperCase())) {
			case WEEK -> ranking.getWeeklyPoint();
			case MONTH -> ranking.getMonthlyPoint();
			case YEAR -> ranking.getYearlyPoint();
		};
	}
}
