package com.moyoy.domain.ranking;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.moyoy.domain.ranking.dto.RankingUpdate;

class RankingTest {

	@Test
	void update_메서드가_정상적으로_필드를_갱신한다() {

		// given
		Ranking ranking = Ranking.createInitial(123L);
		RankingUpdate update = new RankingUpdate("A", 100L, 500L, 1000L);

		// when
		ranking.update(update);

		// then
		assertThat(ranking.getGrade()).isEqualTo("A");
		assertThat(ranking.getWeeklyPoint()).isEqualTo(100L);
		assertThat(ranking.getMonthlyPoint()).isEqualTo(500L);
		assertThat(ranking.getYearlyPoint()).isEqualTo(1000L);
	}

}
