package com.moyoy.domain.ranking;

import java.util.Optional;

public interface RankingRepository {

	Optional<Ranking> findById(Long id);

	Optional<Ranking> findByUserId(Long userId);

	void save(Ranking ranking);
}
