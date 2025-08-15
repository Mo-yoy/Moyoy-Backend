package com.moyoy.batch.domain.ranking.component.writer;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.batch.domain.ranking.component.dto.RankingBatchResult;
import com.moyoy.batch.domain.ranking.component.dto.RankingBatchStats;
import com.moyoy.batch.jobRepository.ranking.RankingBatchDetail;
import com.moyoy.domain.ranking.Ranking;
import com.moyoy.domain.ranking.RankingRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchResultWriter {

	private final RankingRepository rankingRepository;
	private final RankingBatchDetailUpdater rankingBatchDetailUpdater;

	public RankingBatchStats collectResultsAndUpdate(Long batchId, List<RankingBatchResult> rankingBatchResultList) {

		int successCount = 0, failCount = 0;
		List<Ranking> rankings = new ArrayList<>();
		List<RankingBatchDetail> rankingBatchDetails = new ArrayList<>();

		for (RankingBatchResult rankingBatchResult : rankingBatchResultList) {

			if (rankingBatchResult.isSuccess()) {

				rankings.add(rankingBatchResult.getRanking());
				rankingBatchDetails.add(RankingBatchDetail.success(batchId, rankingBatchResult.getRanking().getId()));
				successCount++;
			} else {

				rankingBatchDetails.add(RankingBatchDetail.fail(batchId, rankingBatchResult.getUserId(), rankingBatchResult.getErrorMessage()));
				failCount++;
			}
		}

		///  TODO 작동 방식이 벌크성 연산이 아님
		rankingRepository.saveAll(rankings);
		rankingBatchDetailUpdater.updateAll(rankingBatchDetails);

		return RankingBatchStats.of(successCount, failCount);
	}
}
