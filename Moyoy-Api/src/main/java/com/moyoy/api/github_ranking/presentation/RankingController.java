package com.moyoy.api.github_ranking.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moyoy.api.common.annotation.LoginUserId;
import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.api.github_ranking.business.RankingSearch;
import com.moyoy.api.github_ranking.business.RankingSearchResult;
import com.moyoy.api.github_ranking.business.RankingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RankingController {

	private final RankingService rankingService;

	@GetMapping("/rankings")
	public ResponseEntity<ApiResponse<RankingSearchResponse>> getAllUserRanking(
		@RequestParam("period") String period,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "size", required = false, defaultValue = "20") int size) {

		RankingSearch rankingSearch = new RankingSearch(period, page, size);
		RankingSearchResult rankingSearchResult = rankingService.searchAllUserRanking(rankingSearch);

		RankingSearchResponse responseData = RankingSearchResponse.from(rankingSearchResult, period);

		return ResponseEntity.ok(ApiResponse.success(responseData));
	}

	@GetMapping("/users/me/followings/rankings")
	public ResponseEntity<ApiResponse<RankingSearchResponse>> getAllFollowingRanking(
		@LoginUserId Long userId,
		@RequestParam("period") String period,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "size", required = false, defaultValue = "20") int size) {

		RankingSearch rankingSearch = new RankingSearch(period, page, size);
		RankingSearchResult rankingSearchResult = rankingService.searchUserFollowingsRanking(userId, rankingSearch);

		RankingSearchResponse responseData = RankingSearchResponse.from(rankingSearchResult, period);

		return ResponseEntity.ok(ApiResponse.success(responseData));
	}
}
