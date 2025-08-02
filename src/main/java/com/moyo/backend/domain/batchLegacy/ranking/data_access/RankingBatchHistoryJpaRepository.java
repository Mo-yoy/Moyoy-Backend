package com.moyo.backend.domain.batchLegacy.ranking.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyo.backend.domain.batchLegacy.ranking.implement.RankingBatchHistory;

public interface RankingBatchHistoryJpaRepository extends JpaRepository<RankingBatchHistory, Long> {}
