package com.moyoy.domain.ranking.data_access;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.implement.Ranking;
import com.moyoy.domain.ranking.implement.RankingPeriod;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

	private final RankingJpaRepository rankingJpaRepository;
	private final RankingQueryDslRepository rankingQueryDslRepository;

	@Override
	public void update(Ranking ranking) {
		rankingJpaRepository.save(ranking);
	}

	@Override
	public Optional<Ranking> findById(Long userId) {
		return rankingJpaRepository.findById(userId);
	}

	@Override
	public Slice<Ranking> findAll(RankingPeriod duration, Pageable pageable) {

		return rankingQueryDslRepository.findAll(duration, pageable);
	}

	@Override
	public void save(Ranking ranking) {
		rankingJpaRepository.save(ranking);
	}

	@Override
	public Ranking findByUserId(Long userId) {
		return rankingJpaRepository.findByUserId(userId);
	}

	@Override
	public Slice<Ranking> findFollowingUserRankings(List<Integer> followingUserIds, RankingPeriod rankingPeriod, Pageable pageable) {
		return rankingQueryDslRepository.findByUserIds(followingUserIds, rankingPeriod, pageable);
	}

	@Override
	public void updateAll(List<Ranking> updatedRankings) {
		rankingJpaRepository.saveAll(updatedRankings);
	}
}
