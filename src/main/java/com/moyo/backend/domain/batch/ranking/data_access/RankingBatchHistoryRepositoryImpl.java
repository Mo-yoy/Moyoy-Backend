package com.moyo.backend.domain.batch.ranking.data_access;

import org.springframework.stereotype.Repository;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankingBatchHistoryRepositoryImpl implements RankingBatchHistoryRepository {

	private final RankingBatchHistoryJpaRepository rankingBatchHistoryJpaRepository;

	@Override
	public void save(RankingBatchHistory rankingBatchHistory) {
		rankingBatchHistoryJpaRepository.save(rankingBatchHistory);
	}
}
