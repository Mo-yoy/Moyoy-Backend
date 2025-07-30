package com.moyo.backend.domain.batch.ranking.implement;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.data_access.RankingBatchHistoryRepository;

@Component
@RequiredArgsConstructor
public class RankingBatchHistoryUpdater {

	private final RankingBatchHistoryRepository rankingBatchHistoryRepository;

	public void updateFinalResult(RankingBatchHistory rankingBatchHistory, List<RankingBatchResult> rankingBatchResults) {

		int totalSuccess = rankingBatchResults.stream()
			.mapToInt(RankingBatchResult::successCount)
			.sum();

		int totalFail = rankingBatchResults.stream()
			.mapToInt(RankingBatchResult::failCount)
			.sum();

		rankingBatchHistory.finalizeBatch(totalSuccess, totalFail);

		rankingBatchHistoryRepository.save(rankingBatchHistory);
	}

	public void update(RankingBatchHistory rankingBatchHistory) {

		rankingBatchHistoryRepository.save(rankingBatchHistory);
	}
}
