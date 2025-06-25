package com.moyo.backend.domain.github_ranking.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyo.backend.domain.github_ranking.implement.Ranking;

public interface RankingJpaRepository extends JpaRepository<Ranking, Long> {}
