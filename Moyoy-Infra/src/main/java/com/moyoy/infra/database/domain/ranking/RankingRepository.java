package com.moyoy.infra.database.domain.ranking;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.moyoy.common.enums.RankingPeriod;

public interface RankingRepository {

	void save(RankingEntity ranking);
	void saveAll(List<RankingEntity> rankings);
	Optional<RankingEntity> findById(Long userId);

	Slice<RankingEntity> findAll(RankingPeriod duration, Pageable pageable);

	Slice<RankingEntity> findFollowingUserRankings(List<Integer> followingUserIds, RankingPeriod rankingPeriod, Pageable pageable);
}
