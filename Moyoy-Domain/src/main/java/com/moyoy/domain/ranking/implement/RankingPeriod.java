package com.moyoy.domain.ranking.implement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankingPeriod {

	WEEK("week", 7),
	MONTH("month", 31),
	YEAR("year", 365);

	private final String value;
	private final long weight;
}
