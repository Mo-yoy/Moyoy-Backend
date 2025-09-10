package com.moyoy.api.ranking.presentation.response;

import java.util.List;

import com.moyoy.api.ranking.application.response.RankingSearchResult;

import com.moyoy.infra.database.mysql.query.dto.UserRankingView;

public record RankingListResponse(
	List<RankingDetails> rankingList,
	boolean lastPage) {

	public static RankingListResponse from(RankingSearchResult rankingSearchResult) {

		List<RankingDetails> rankingDetailsList = rankingSearchResult.userRankingViews()
			.stream()
			.map(RankingDetails::from)
			.toList();

		return new RankingListResponse(rankingDetailsList, rankingSearchResult.isLast());
	}

	record RankingDetails(
		Long userId,
		String profileImageUrl,
		String username,
		long rankPoint) {

		public static RankingDetails from(UserRankingView userRankingView) {

			return new RankingDetails(
				userRankingView.userId(),
				userRankingView.profileImageUrl(),
				userRankingView.username(),
				userRankingView.rankPoint());
		}
	}
}
