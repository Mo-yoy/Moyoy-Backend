package com.moyo.backend.domain.github_ranking.business;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.moyo.backend.domain.github_ranking.implement.RankingReader;
import com.moyo.backend.domain.github_ranking.implement.RankingSearch;
import com.moyo.backend.domain.user.implement.User;

@Service
@RequiredArgsConstructor
public class RankingService {

	private final RankingReader rankingReader;

	public RankingSearchResult getAllUserRanking(String duration, int page, int size) {

		RankingSearch rankingSearch = new RankingSearch(duration, page, size);
		Slice<User> allUserRankings = rankingReader.getAllUserRankings(rankingSearch);

		return RankingSearchResult.from(allUserRankings, rankingSearch.duration());
	}
}
