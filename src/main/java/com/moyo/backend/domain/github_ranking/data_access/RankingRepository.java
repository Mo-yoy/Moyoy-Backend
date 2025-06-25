package com.moyo.backend.domain.github_ranking.data_access;

import com.moyo.backend.domain.github_ranking.implement.Ranking;

public interface RankingRepository {

	void update(Ranking ranking);
}
