package com.moyo.backend.domain.batch.ranking.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingUpdater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingBatchManager {

	private final RankingUpdater rankingUpdater;
	private final RankingBatchDetailUpdater rankingBatchDetailUpdater;

	public void updateRankings(Long batchId, List<Future<Ranking>> rankingFutures) {

		List<Ranking> rankings = new ArrayList<>();
		List<RankingBatchDetail> rankingBatchDetails = new ArrayList<>();

		rankingFutures.forEach(rankingFuture -> {

			try {
				Ranking ranking = rankingFuture.get();
				rankings.add(ranking);

				RankingBatchDetail rankingBatchDetail = RankingBatchDetail.success(batchId, ranking.getId());
				rankingBatchDetails.add(rankingBatchDetail);
			}
			catch (InterruptedException e) {
				throw new RuntimeException("인터럽트 발생");
			}
			catch (ExecutionException e) {
				throw new RuntimeException("에러 발생");
				// 랭킹 배치 예외 따로 만들어서 관리
				// 예외로 부터 랭킹 아이디랑 에러 이유를 뽑아 내야 함.
				RankingBatchDetail rankingBatchDetail = RankingBatchDetail.fail(batchId, rankingId, errorMessage);
				rankingBatchDetails.add(rankingBatchDetail);
			}
		});

		///  TODO 작동 방식이 벌크성 연산이 아님
		rankingUpdater.updateAll(rankings);
		rankingBatchDetailUpdater.updateAll(rankingBatchDetails);
	}
}
