package com.moyo.backend.domain.github_ranking.implement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankingDuration {

	WEEK("week", "weeklyPoint", 7),
	MONTH("month", "monthlyPoint", 31),
	YEAR("year", "yearlyPoint", 365);

	private final String value;
	private final String attributeName;
	private final long weight;
}
