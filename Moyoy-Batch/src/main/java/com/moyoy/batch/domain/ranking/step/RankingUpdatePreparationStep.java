package com.moyoy.batch.domain.ranking.step;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.batch.domain.ranking.component.dto.RankingBatchResult;
import com.moyoy.batch.domain.ranking.step.dto.RankingUpdateParameters;
import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingRepository;
import com.moyoy.domain.ranking.RankingUpdate;
import com.moyoy.domain.support.error.ranking.RankingNotFoundException;

@Component
@RequiredArgsConstructor
public class RankingUpdatePreparationStep {

	private final RankingRepository rankingRepository;

	public void execute(RankingUpdateParameters rankingUpdateParameters, RankingBatchResult rankingBatchResult) {

		Ranking ranking = rankingRepository.findById(rankingUpdateParameters.currentUserId()).orElseThrow(RankingNotFoundException::new);

		String grade = rankingUpdateParameters.rankingCalculatorResult().rankingGrade();
		long weeklyPoint = rankingUpdateParameters.rankingCalculatorResult().weekRankingPoint();
		long monthlyPoint = rankingUpdateParameters.rankingCalculatorResult().monthRankingPoint();
		long yearlyPoint = rankingUpdateParameters.rankingCalculatorResult().yearRankingPoint();

		RankingUpdate rankingUpdate = new RankingUpdate(grade, weeklyPoint, monthlyPoint, yearlyPoint);

		ranking.update(rankingUpdate);

		rankingBatchResult.updateOnSuccess(ranking);
	}
}
