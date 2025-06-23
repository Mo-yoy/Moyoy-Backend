package com.moyo.backend.domain.github_ranking.implement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankingDuration {

	WEEK("week", "weeklyPoint"),
	MONTH("month", "monthlyPoint"),
	YEAR("year", "yearlyPoint");

	private final String value;
	private final String attributeName;
}
