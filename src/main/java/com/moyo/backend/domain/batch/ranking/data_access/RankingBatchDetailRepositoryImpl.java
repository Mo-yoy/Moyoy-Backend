package com.moyo.backend.domain.batch.ranking.data_access;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchDetail;

@Repository
@RequiredArgsConstructor
public class RankingBatchDetailRepositoryImpl implements RankingBatchDetailRepository {

	private final RankingBatchDetailJpaRepository rankingBatchDetailJpaRepository;

	@Override
	public void updateAll(List<RankingBatchDetail> rankingBatchDetails) {
		rankingBatchDetailJpaRepository.saveAll(rankingBatchDetails);
	}
}
