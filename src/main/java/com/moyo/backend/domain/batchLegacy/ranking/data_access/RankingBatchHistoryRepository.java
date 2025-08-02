package com.moyo.backend.domain.batchLegacy.ranking.data_access;

import com.moyo.backend.domain.batchLegacy.ranking.implement.RankingBatchHistory;

public interface RankingBatchHistoryRepository {
	void save(RankingBatchHistory rankingBatchHistory);

	RankingBatchHistory findById(Long rankingBatchHistoryId);
}
