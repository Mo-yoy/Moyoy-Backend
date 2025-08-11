package com.moyoy.batch.domain.ranking.step;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.batch.domain.ranking.component.processor.RankingCalculator;
import com.moyoy.batch.domain.ranking.step.dto.RankingCalculatorParameters;
import com.moyoy.batch.domain.ranking.step.dto.RankingCalculatorResult;

@Component
@RequiredArgsConstructor
public class RankingCalculatorStep {

	private final RankingCalculator rankingCalculator;

	public RankingCalculatorResult execute(RankingCalculatorParameters rankingCalculatorParameters) {

		return rankingCalculator.calculate(rankingCalculatorParameters);
	}
}
