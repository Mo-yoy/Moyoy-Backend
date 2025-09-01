package com.moyoy.batch.legacy.domain.ranking.step;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.RankingCalculator;
import com.moyoy.domain.ranking.RankingCalculatorParameters;
import com.moyoy.domain.ranking.RankingCalculatorResult;

@Component
@RequiredArgsConstructor
public class RankingCalculatorStep {

	private final RankingCalculator rankingCalculator;

	public RankingCalculatorResult execute(RankingCalculatorParameters rankingCalculatorParameters) {

		return rankingCalculator.calculate(rankingCalculatorParameters);
	}
}
