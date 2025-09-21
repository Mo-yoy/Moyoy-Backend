package com.moyoy.infra.database.mysql.ranking;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingJpaRepository extends JpaRepository<RankingEntity, Long> {
	Optional<RankingEntity> findByUserId(Long userId);
}
