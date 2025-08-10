package com.moyoy.domain.ranking.data_access;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.moyoy.domain.ranking.implement.Ranking;
import com.moyoy.domain.ranking.implement.RankingPeriod;

public interface RankingRepository {

	void update(Ranking ranking);

	Optional<Ranking> findById(Long userId);

	Slice<Ranking> findAll(RankingPeriod duration, Pageable pageable);

	void save(Ranking ranking);

	Ranking findByUserId(Long userId);

	Slice<Ranking> findFollowingUserRankings(List<Integer> followingUserIds, RankingPeriod rankingPeriod, Pageable pageable);

	void updateAll(List<Ranking> updatedRankings);
}
