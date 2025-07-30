package com.moyo.backend.domain.batch.ranking.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyo.backend.domain.batch.ranking.implement.RankingBatchHistory;

public interface RankingBatchHistoryJpaRepository extends JpaRepository<RankingBatchHistory, Long> {}
