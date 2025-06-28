package com.moyo.backend.domain.github_ranking.data_access;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.implement.Ranking;
import com.moyo.backend.domain.github_ranking.implement.RankingPeriod;

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
}
