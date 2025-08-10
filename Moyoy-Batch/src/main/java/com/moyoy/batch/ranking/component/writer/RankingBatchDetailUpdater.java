package com.moyoy.batch.ranking.component.writer;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyoy.batch.jobRepository.ranking.RankingBatchDetail;
import com.moyoy.batch.jobRepository.ranking.RankingBatchDetailRepository;

@Component
@RequiredArgsConstructor
public class RankingBatchDetailUpdater {

	private final RankingBatchDetailRepository rankingBatchDetailRepository;

	public void updateAll(List<RankingBatchDetail> rankingBatchDetails) {
		rankingBatchDetailRepository.saveAll(rankingBatchDetails);
	}
}
