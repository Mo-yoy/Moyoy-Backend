package com.moyoy.core.domain.ranking.business;

import com.moyoy.common.enums.RankingPeriod;

public record RankingSearch(
	RankingPeriod period,
	int page,
	int size) {

	public RankingSearch(String period, int page, int size) {
		this(RankingPeriod.valueOf(period.toUpperCase()), page, size);
	}
}
