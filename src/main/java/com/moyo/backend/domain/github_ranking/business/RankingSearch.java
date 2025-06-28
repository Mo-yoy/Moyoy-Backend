package com.moyo.backend.domain.github_ranking.business;

import com.moyo.backend.domain.github_ranking.implement.RankingPeriod;

public record RankingSearch(
	RankingPeriod period,
	int page,
	int size) {

	public RankingSearch(
		String period,
		int page,
		int size) {
		this(RankingPeriod.valueOf(period.toUpperCase()), page, size);
	}
}
