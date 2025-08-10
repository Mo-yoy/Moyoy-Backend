package com.moyoy.batch.ranking.job;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.batch.jobRepository.ranking.RankingBatchHistory;
import com.moyoy.batch.jobRepository.ranking.RankingBatchHistoryRepository;
import com.moyoy.batch.ranking.component.dto.RankingBatchStats;

@Component
@RequiredArgsConstructor
public class RankingBatchHistoryUpdater {

	private final RankingBatchHistoryRepository rankingBatchHistoryRepository;

	public void updateFinalResult(RankingBatchHistory rankingBatchHistory, List<RankingBatchStats> rankingBatchStats) {

		int totalSuccess = rankingBatchStats.stream()
			.mapToInt(RankingBatchStats::successCount)
			.sum();

		int totalFail = rankingBatchStats.stream()
			.mapToInt(RankingBatchStats::failCount)
			.sum();

		int totalCount = totalSuccess + totalFail;

		rankingBatchHistory.finalizeBatch(totalSuccess, totalFail, totalCount);

		rankingBatchHistoryRepository.save(rankingBatchHistory);
	}
}
