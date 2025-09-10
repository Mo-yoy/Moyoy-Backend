package com.moyoy.api.ranking.application.response;

import java.util.List;

import com.moyoy.infra.database.mysql.query.dto.UserRankingView;

import com.moyoy.common.page.SliceResult;

public record RankingSearchResult(
	List<UserRankingView> userRankingViews,
	boolean isLast) {

	public static RankingSearchResult from(SliceResult<UserRankingView> rankingViewSlice) {
		return new RankingSearchResult(rankingViewSlice.content(), rankingViewSlice.isLast());
	}
}
