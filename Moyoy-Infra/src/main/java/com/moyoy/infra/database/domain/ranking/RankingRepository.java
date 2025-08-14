package com.moyoy.infra.database.domain.ranking;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.moyoy.core.domain.ranking.implement.Ranking;
import com.moyoy.core.domain.ranking.implement.RankingPeriod;

public interface RankingRepository {

	void save(Ranking ranking);

	Optional<Ranking> findById(Long userId);

	void update(Ranking ranking);

	Slice<Ranking> findAll(RankingPeriod duration, Pageable pageable);

	Slice<Ranking> findFollowingUserRankings(List<Integer> followingUserIds, RankingPeriod rankingPeriod, Pageable pageable);

	void updateAll(List<Ranking> updatedRankings);
}
