package com.moyo.backend.domain.github_ranking.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_ranking.data_access.RankingRepository;

@Component
@RequiredArgsConstructor
public class RankingReader {

	private final RankingRepository rankingRepository;

	public Ranking getRanking(Long userId) {

		return rankingRepository.findById(userId).orElseThrow();
	}

	public RankingSlice getAllRanking(RankingPeriod rankingPeriod, int page, int size) {

		Pageable pageable = PageRequest.of(page, size);

		Slice<Ranking> rankingSlice = rankingRepository.findAll(rankingPeriod, pageable);

		return new RankingSlice(rankingSlice.getContent(), rankingSlice.hasNext());
	}
}
