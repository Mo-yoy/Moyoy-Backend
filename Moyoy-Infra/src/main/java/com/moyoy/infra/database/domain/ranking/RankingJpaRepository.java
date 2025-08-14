package com.moyoy.infra.database.domain.ranking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingJpaRepository extends JpaRepository<RankingEntity, Long> {
	RankingEntity findByUserId(Long userId);
}
