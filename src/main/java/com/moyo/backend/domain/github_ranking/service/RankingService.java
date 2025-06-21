package com.moyo.backend.domain.github_ranking.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.moyo.backend.domain.github_ranking.implement.RankingReader;
import com.moyo.backend.domain.github_ranking.presentation.dto.RankingSearchRequest;
import com.moyo.backend.domain.github_ranking.presentation.dto.RankingSearchResponse;
import com.moyo.backend.domain.user.domain.User;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final RankingReader rankingReader;

	public RankingSearchResponse getAllUserRanking(RankingSearchRequest request) {

		Slice<User> allUserRankings = rankingReader.getAllUserRankings(request);

		return RankingSearchResponse.from(allUserRankings, request.getDuration());
	}
}
