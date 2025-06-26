package com.moyo.backend.domain.github_ranking.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.data_access.RankingRepository;

import jakarta.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class RankingUpdater {

	private final RankingRepository rankingRepository;

	@Transactional
	public void update(Ranking ranking) {
		rankingRepository.update(ranking);
	}
}
