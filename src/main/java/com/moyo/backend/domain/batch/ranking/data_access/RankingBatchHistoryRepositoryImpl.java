package com.moyo.backend.domain.batch.ranking.data_access;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistory;

@Repository
@RequiredArgsConstructor
public class RankingBatchHistoryRepositoryImpl implements RankingBatchHistoryRepository {

	private final RankingBatchHistoryJpaRepository rankingBatchHistoryJpaRepository;

	@Override
	public void save(RankingBatchHistory rankingBatchHistory) {
		rankingBatchHistoryJpaRepository.save(rankingBatchHistory);
	}

	@Override
	public RankingBatchHistory findById(Long rankingBatchHistoryId) {
		return rankingBatchHistoryJpaRepository.findById(rankingBatchHistoryId).orElse(null);
	}
}
