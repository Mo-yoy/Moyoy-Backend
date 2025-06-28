package com.moyo.backend.domain.github_ranking.data_access;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingPeriod;

public interface RankingRepository {

	void update(Ranking ranking);

	Optional<Ranking> findById(Long userId);

	Slice<Ranking> findAll(RankingPeriod duration, Pageable pageable);

	void save(Ranking ranking);

	Ranking findByUserId(Long userId);
}
