package com.moyo.backend.domain.batch.ranking.implement;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batch.ranking.data_access.RankingBatchDetailRepository;

@Component
@RequiredArgsConstructor
public class RankingBatchDetailUpdater {

	private final RankingBatchDetailRepository rankingBatchDetailRepository;

	public void updateAll(List<RankingBatchDetail> rankingBatchDetails) {
		rankingBatchDetailRepository.updateAll(rankingBatchDetails);
	}
}
