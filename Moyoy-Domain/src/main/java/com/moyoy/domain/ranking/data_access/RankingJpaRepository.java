package com.moyoy.domain.ranking.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyoy.domain.ranking.implement.Ranking;

public interface RankingJpaRepository extends JpaRepository<Ranking, Long> {
	Ranking findByUserId(Long userId);
}
