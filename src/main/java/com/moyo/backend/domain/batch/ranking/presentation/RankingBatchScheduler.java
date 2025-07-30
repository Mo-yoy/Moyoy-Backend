package com.moyo.backend.domain.batch.ranking.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.business.RankingBatchPreparationResult;
import com.moyo.backend.domain.batch.ranking.business.RankingBatchPreparationService;
import com.moyo.backend.domain.batch.ranking.business.RankingBatchRequest;
import com.moyo.backend.domain.batch.ranking.business.RankingBatchService;

/**
 *  기존 Layer 컨벤션에 혼동을 주지 않기 위해서
 *
 *  scheduler Layer == presentation Layer
 *  Scheduler == Controller
 *
 *  이렇게 취급하기로 했습니다. 모든 컨벤션은 동일하게 적용됩니다.
 */

@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

	private final RankingBatchPreparationService rankingBatchPreparationService;
	private final RankingBatchService rankingBatchService;

	@Scheduled(cron = "0 0/5 * * * *")
	public void dailyRankingBatch() {

		RankingBatchPreparationResult preparationResult = rankingBatchPreparationService.prepareRankingBatch();

		RankingBatchRequest rankingBatchRequest = new RankingBatchRequest(
			preparationResult.userRankingBatchSnapshot().lastUserId(),
			preparationResult.rankingBatchHistory()
		);

		rankingBatchService.rankingBatch(rankingBatchRequest);
	}

}
