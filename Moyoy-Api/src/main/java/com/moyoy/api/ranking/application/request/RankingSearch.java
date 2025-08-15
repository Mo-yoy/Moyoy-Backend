package com.moyoy.api.ranking.application.request;

import com.moyoy.domain.ranking.RankingPeriod;

public record RankingSearch(
	RankingPeriod period,
	int page,
	int size) {

	public RankingSearch(String period, int page, int size) {
		this(RankingPeriod.valueOf(period.toUpperCase()), page, size);
	}
}
