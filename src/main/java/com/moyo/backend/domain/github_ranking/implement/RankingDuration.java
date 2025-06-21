package com.moyo.backend.domain.github_ranking.implement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankingDuration {

	WEEK("weeklyPoint"),
	MONTH("monthlyPoint"),
	YEAR("yearlyPoint");

	private final String value;
}
