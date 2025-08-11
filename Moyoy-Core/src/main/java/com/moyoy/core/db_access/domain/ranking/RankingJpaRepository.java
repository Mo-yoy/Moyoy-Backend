package com.moyoy.core.db_access.domain.ranking;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyoy.core.domain.ranking.implement.Ranking;

public interface RankingJpaRepository extends JpaRepository<Ranking, Long> {
	Ranking findByUserId(Long userId);
}
