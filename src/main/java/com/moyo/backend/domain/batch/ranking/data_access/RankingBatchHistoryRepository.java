package com.moyo.backend.domain.batch.ranking.data_access;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistory;

public interface RankingBatchHistoryRepository {
	void save(RankingBatchHistory rankingBatchHistory);
}
