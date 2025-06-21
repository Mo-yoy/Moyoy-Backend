package com.moyo.backend.domain.github_ranking.presentation;

import java.util.List;

import com.moyo.backend.domain.github_ranking.business.RankingSearchResult;
import com.moyo.backend.domain.github_ranking.business.UserRankingDetails;

public record RankingSearchResponse(
	List<UserRankingDetails> userList,
	boolean lastPage) {

	public static RankingSearchResponse from(RankingSearchResult rankingSearchResult) {
		return new RankingSearchResponse(
			rankingSearchResult.userRankingDetailsSlice().getContent(),
			rankingSearchResult.userRankingDetailsSlice().isLast());
	}
}
