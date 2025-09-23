package com.moyoy.domain.ranking;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.moyoy.domain.ranking.dto.RankingCalculatorParameters;
import com.moyoy.domain.ranking.dto.RankingCalculatorResult;

class RankingCalculatorTest {

	private RankingCalculator rankingCalculator;

	@BeforeEach
	void setUp() {
		rankingCalculator = new RankingCalculator();
	}

	@Test
	void 주간_월간_연간_점수와_등급을_계산한다() {

		// given
		RankingCalculatorParameters params = new RankingCalculatorParameters(
			50,
			100,
			new RankingCalculatorParameters.CommitStatsSummary(10, 500), // week
			new RankingCalculatorParameters.CommitStatsSummary(40, 2000), // month
			new RankingCalculatorParameters.CommitStatsSummary(200, 10000) // year
		);

		// when
		RankingCalculatorResult result = rankingCalculator.calculate(params);

		// then
		assertThat(result.weekRankingPoint()).isGreaterThan(0);
		assertThat(result.monthRankingPoint()).isGreaterThan(0);
		assertThat(result.yearRankingPoint()).isGreaterThan(0);

		assertThat(result.rankingGrade()).isIn("S", "A+", "A", "A-", "B+", "B", "B-", "C+", "C");
	}

	@Test
	void 활동이_없으면_최하위_등급을_부여한다() {

		// given
		RankingCalculatorParameters params = new RankingCalculatorParameters(
			0, 0,
			new RankingCalculatorParameters.CommitStatsSummary(0, 0),
			new RankingCalculatorParameters.CommitStatsSummary(0, 0),
			new RankingCalculatorParameters.CommitStatsSummary(0, 0));

		// when
		RankingCalculatorResult result = rankingCalculator.calculate(params);

		// then
		assertThat(result.weekRankingPoint()).isEqualTo(0);
		assertThat(result.monthRankingPoint()).isEqualTo(0);
		assertThat(result.yearRankingPoint()).isEqualTo(0);
		assertThat(result.rankingGrade()).isEqualTo("C");
	}
}
