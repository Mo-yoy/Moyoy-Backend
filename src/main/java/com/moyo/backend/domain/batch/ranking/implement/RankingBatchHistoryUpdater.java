package com.moyo.backend.domain.batch.ranking.implement;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.data_access.RankingBatchHistoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingBatchHistoryUpdater {

	private final RankingBatchHistoryRepository rankingBatchHistoryRepository;

	public void update(RankingBatchHistory rankingBatchHistory) {

		rankingBatchHistoryRepository.save(rankingBatchHistory);
	}
}
