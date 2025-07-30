package com.moyo.backend.domain.batch.ranking.data_access;

import java.util.List;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchDetail;

public interface RankingBatchDetailRepository {
	void updateAll(List<RankingBatchDetail> rankingBatchDetails);
}
