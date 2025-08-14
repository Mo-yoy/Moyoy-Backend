package com.moyoy.infra.database.domain.ranking;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.moyoy.common.enums.RankingPeriod;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

	private final RankingJpaRepository rankingJpaRepository;
	private final RankingQueryDslRepository rankingQueryDslRepository;

	@Override
	public void save(RankingEntity ranking) {
		rankingJpaRepository.save(ranking);
	}

	@Override
	public void saveAll(List<RankingEntity> updatedRankings) {
		rankingJpaRepository.saveAll(updatedRankings);
	}

	@Override
	public Optional<RankingEntity> findById(Long userId) {
		return rankingJpaRepository.findById(userId);
	}

	@Override
	public Slice<RankingEntity> findAll(RankingPeriod duration, Pageable pageable) {

		return rankingQueryDslRepository.findAll(duration, pageable);
	}

	@Override
	public Slice<RankingEntity> findFollowingUserRankings(List<Integer> followingUserIds, RankingPeriod rankingPeriod, Pageable pageable) {
		return rankingQueryDslRepository.findByUserIds(followingUserIds, rankingPeriod, pageable);
	}
}
