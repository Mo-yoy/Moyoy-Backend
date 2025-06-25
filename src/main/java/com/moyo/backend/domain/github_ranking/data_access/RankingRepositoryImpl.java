package com.moyo.backend.domain.github_ranking.data_access;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.implement.Ranking;

@Component
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

	private final RankingJpaRepository rankingJpaRepository;

	@Override
	public void update(Ranking ranking) {
		rankingJpaRepository.save(ranking);
	}

	@Override
	public Optional<Ranking> findById(Long userId) {
		return rankingJpaRepository.findById(userId);
	}
}
