package com.moyo.backend.domain.batch.ranking.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.data_access.RankingBatchHistoryRepository;

@Component
@RequiredArgsConstructor
public class RankingBatchHistoryReader {

	private final RankingBatchHistoryRepository rankingBatchHistoryRepository;

	public RankingBatchHistory findById(Long rankingBatchHistoryId) {

		return rankingBatchHistoryRepository.findById(rankingBatchHistoryId);
	}
}
