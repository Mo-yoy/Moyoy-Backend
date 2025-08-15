package com.moyoy.infra.database.domain.ranking;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface RankingRepository {

	void save(RankingEntity ranking);
	void saveAll(List<RankingEntity> rankings);
	Optional<RankingEntity> findById(Long userId);

	Slice<RankingEntity> findAll(String duration, Pageable pageable);

	Slice<RankingEntity> findFollowingUserRankings(List<Integer> followingUserIds, String rankingPeriod, Pageable pageable);
}
