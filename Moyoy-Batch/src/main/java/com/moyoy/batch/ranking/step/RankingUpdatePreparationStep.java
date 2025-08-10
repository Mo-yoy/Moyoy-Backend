package com.moyoy.batch.ranking.step;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.batch.ranking.job.RankingBatchResult;
import com.moyoy.batch.ranking.step.dto.RankingUpdateParameters;
import com.moyoy.domain.ranking.implement.Ranking;
import com.moyoy.domain.ranking.implement.RankingReader;
import com.moyoy.domain.ranking.implement.RankingUpdate;

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
