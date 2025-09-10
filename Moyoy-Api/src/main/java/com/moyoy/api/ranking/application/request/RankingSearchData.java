package com.moyoy.api.ranking.application.request;

import com.moyoy.domain.ranking.RankingPeriod;

public record RankingSearchData(
	RankingPeriod period,
	int page,
	int size) {

	public static RankingSearchData of(String period, int page, int size) {
		RankingPeriod rankingPeriod = RankingPeriod.valueOf(period.toUpperCase());
		return new RankingSearchData(rankingPeriod, page, size);
	}
}
