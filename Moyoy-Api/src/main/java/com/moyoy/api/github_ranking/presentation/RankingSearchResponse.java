package com.moyoy.api.github_ranking.presentation;

import java.util.List;

import com.moyoy.api.github_ranking.business.RankingSearchResult;

public record RankingSearchResponse(
	List<UserAndRanking> userList,
	boolean lastPage) {

	public static RankingSearchResponse from(RankingSearchResult rankingSearchResult, String period) {

		List<UserAndRanking> userList = rankingSearchResult.rankingWithUsers().stream()
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
