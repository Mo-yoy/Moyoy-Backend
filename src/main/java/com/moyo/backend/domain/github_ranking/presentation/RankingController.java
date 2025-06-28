package com.moyo.backend.domain.github_ranking.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moyo.backend.common.response.ApiResponse;
import com.moyo.backend.domain.github_ranking.business.RankingSearch;
import com.moyo.backend.domain.github_ranking.business.RankingSearchResult;
import com.moyo.backend.domain.github_ranking.business.RankingService;

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

		RankingSearchResponse rankingSearchResponse = RankingSearchResponse.from(rankingSearchResult, period);

		return ResponseEntity.ok(ApiResponse.success(rankingSearchResponse));
	}
}
