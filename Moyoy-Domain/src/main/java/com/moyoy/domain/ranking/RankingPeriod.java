package com.moyoy.domain.ranking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankingPeriod {

	WEEK("week", 1.0 / 52),
	MONTH("month", 1.0 / 12),
	YEAR("year", 1.0);

	private final String value;
	private final double weight;
}
