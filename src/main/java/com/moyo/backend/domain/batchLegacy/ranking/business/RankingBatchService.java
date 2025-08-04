package com.moyo.backend.domain.batchLegacy.ranking.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batchLegacy.ranking.implement.RankingBatchManager;
import com.moyo.backend.domain.batchLegacy.ranking.implement.RankingBatchRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchService {

	private final RankingBatchManager rankingBatchManager;

	public void launchRankingBatch() {

		RankingBatchPreparationResult preparationResult = rankingBatchManager.prepareRankingBatch();

		RankingBatchRequest rankingBatchRequest = RankingBatchRequest.from(preparationResult);

		rankingBatchManager.rankingBatch(rankingBatchRequest);
	}
}
