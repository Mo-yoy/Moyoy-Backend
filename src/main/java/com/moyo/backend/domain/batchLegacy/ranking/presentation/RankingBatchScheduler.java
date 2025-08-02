package com.moyo.backend.domain.batchLegacy.ranking.presentation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batchLegacy.ranking.business.RankingBatchService;

import lombok.RequiredArgsConstructor;

/**
 *  기존 Layer 컨벤션에 혼동을 주지 않기 위해서
 *  <br/>
 *  scheduler Layer == presentation Layer
 *  <br/>
 *  Scheduler == Controller
 *  <br/>
 *  이렇게 취급하기로 했습니다. 모든 컨벤션은 동일하게 적용됩니다.
 */

@Component
@RequiredArgsConstructor
public class RankingBatchScheduler {

	private final RankingBatchService rankingBatchService;

	@Scheduled(cron = "00 02 * * * *")
	public void dailyRankingBatch() {

		rankingBatchService.launchRankingBatch();
	}
}
