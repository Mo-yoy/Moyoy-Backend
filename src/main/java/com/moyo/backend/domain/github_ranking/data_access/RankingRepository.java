package com.moyo.backend.domain.github_ranking.data_access;

import java.util.Optional;

import com.moyo.backend.domain.github_ranking.implement.Ranking;

public interface RankingRepository {

	void update(Ranking ranking);

	Optional<Ranking> findById(Long userId);
}
