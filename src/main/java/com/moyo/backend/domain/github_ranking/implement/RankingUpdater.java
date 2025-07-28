package com.moyo.backend.domain.github_ranking.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.moyo.backend.domain.github_ranking.data_access.RankingRepository;

@Component
@Transactional
@RequiredArgsConstructor
public class RankingUpdater {

	private final RankingRepository rankingRepository;

	public void update(Ranking ranking) {
		rankingRepository.update(ranking);
	}
}
