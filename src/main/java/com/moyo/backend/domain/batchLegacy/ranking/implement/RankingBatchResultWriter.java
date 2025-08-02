package com.moyo.backend.domain.batchLegacy.ranking.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingUpdater;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchResultWriter {

	private final RankingUpdater rankingUpdater;
	private final RankingBatchDetailUpdater rankingBatchDetailUpdater;

	public RankingBatchResult collectResultsAndUpdate(Long batchId, List<Future<RankingBatchTaskResult>> rankingFutures) {

		int successCount = 0, failCount = 0;
		List<Ranking> rankings = new ArrayList<>();
		List<RankingBatchDetail> rankingBatchDetails = new ArrayList<>();

		for (Future<RankingBatchTaskResult> rankingFuture : rankingFutures) {
			try {
				RankingBatchTaskResult result = rankingFuture.get();

				if (result.success()) {

					rankings.add(result.ranking());
					rankingBatchDetails.add(RankingBatchDetail.success(batchId, result.ranking().getId()));
					successCount++;
				} else {
					rankingBatchDetails.add(RankingBatchDetail.fail(batchId, result.userId(), result.errorMessage()));
					failCount++;
				}
			} catch (InterruptedException e) {
				log.error("랭킹 배치 작업 중 인터럽트 발생", e);
			} catch (ExecutionException e) {
				log.error("랭킹 배치 작업 Future 에서 처리하지 못한 예외 발생", e);
			}
		}

		///  TODO 작동 방식이 벌크성 연산이 아님
		rankingUpdater.updateAll(rankings);
		rankingBatchDetailUpdater.updateAll(rankingBatchDetails);

		return RankingBatchResult.of(successCount, failCount);
	}
}
