package com.moyoy.batch.domain.ranking.step;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.batch.domain.ranking.component.dto.RankingBatchResult;
import com.moyoy.batch.domain.ranking.step.dto.RankingUpdateParameters;
import com.moyoy.core.domain.ranking.implement.Ranking;
import com.moyoy.core.domain.ranking.implement.RankingReader;
import com.moyoy.core.domain.ranking.implement.RankingUpdate;

@Component
@RequiredArgsConstructor
public class RankingUpdatePreparationStep {

	private final RankingReader rankingReader;

	public void execute(RankingUpdateParameters rankingUpdateParameters, RankingBatchResult rankingBatchResult) {

		Ranking ranking = rankingReader.getRanking(rankingUpdateParameters.currentUserId());

		String grade = rankingUpdateParameters.rankingCalculatorResult().rankingGrade();
		long weeklyPoint = rankingUpdateParameters.rankingCalculatorResult().weekRankingPoint();
		long monthlyPoint = rankingUpdateParameters.rankingCalculatorResult().monthRankingPoint();
		long yearlyPoint = rankingUpdateParameters.rankingCalculatorResult().yearRankingPoint();

		RankingUpdate rankingUpdate = new RankingUpdate(grade, weeklyPoint, monthlyPoint, yearlyPoint);

		ranking.updateRankingByBatch(rankingUpdate);

		rankingBatchResult.updateOnSuccess(ranking);
	}
}
