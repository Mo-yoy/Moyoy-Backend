package com.moyoy.api.ranking.presentation.response;

import java.util.List;

import com.moyoy.api.ranking.application.response.RankingSearchResult;

public record RankingSearchResponse(
	List<UserAndRanking> userList,
	boolean lastPage) {

	public static RankingSearchResponse from(RankingSearchResult rankingSearchResult, String period) {

		List<UserAndRanking> userList = rankingSearchResult.rankingUserViews().stream()
			.map(rankingWithUser -> new UserAndRanking(
				rankingWithUser.user().getId(),
				rankingWithUser.user().getProfileImgUrl(),
				rankingWithUser.user().getUsername(),
				rankingSearchResult.getPointByDuration(rankingWithUser.ranking(), period)))
			.toList();

		return new RankingSearchResponse(userList, rankingSearchResult.isLast());
	}

	record UserAndRanking(
		Long userId,
		String profileImageUrl,
		String username,
		long rankPoint) {
	}
}
