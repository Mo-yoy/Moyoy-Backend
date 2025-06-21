package com.moyo.backend.domain.github_ranking.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moyo.backend.common.response.ApiResponse;
import com.moyo.backend.domain.github_ranking.business.RankingSearchResult;
import com.moyo.backend.domain.github_ranking.business.RankingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RankingController {

	private final RankingService rankingService;

	@GetMapping("/rankings")
	public ResponseEntity<ApiResponse<RankingSearchResponse>> getAllUserRanking(
		@RequestParam("duration") String duration,
		@RequestParam("page") int page,
		@RequestParam("size") int size) {

		RankingSearchResult rankingSearchResult = rankingService.getAllUserRanking(duration, page, size);
		RankingSearchResponse responseData = RankingSearchResponse.from(rankingSearchResult);

		return ResponseEntity.ok(ApiResponse.success(responseData));
	}
}
