package com.moyoy.api.ranking.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.api.ranking.application.RankingService;
import com.moyoy.api.ranking.application.request.RankingSearchData;
import com.moyoy.api.ranking.application.response.RankingSearchResult;
import com.moyoy.api.ranking.presentation.request.RankingListRequest;
import com.moyoy.api.ranking.presentation.response.RankingListResponse;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RankingController {

	private final RankingService rankingService;

	@GetMapping("/rankings")
	public ResponseEntity<ApiResponse<RankingListResponse>> getAllRankingList(
		@Valid RankingListRequest request) {

		RankingSearchData data = RankingSearchData.of(request.period(), request.page(), request.size());
		RankingSearchResult result = rankingService.searchAllUserRanking(data);

		RankingListResponse responseData = RankingListResponse.from(result);

		return ResponseEntity.ok(ApiResponse.success(responseData));
	}
}
