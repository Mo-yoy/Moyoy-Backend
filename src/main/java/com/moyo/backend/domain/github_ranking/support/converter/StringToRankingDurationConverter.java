package com.moyo.backend.domain.github_ranking.support.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.implement.RankingDuration;

@Component
public class StringToRankingDurationConverter implements Converter<String, RankingDuration> {

	@Override
	public RankingDuration convert(String input) {
		try {
			return RankingDuration.valueOf(input.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("유효하지 않은 Ranking Duration Type: " + input);
		}
	}
}
