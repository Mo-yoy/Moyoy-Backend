package com.moyo.backend.domain.github_ranking.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyo.backend.common.response.ApiResponse;
import com.moyo.backend.domain.github_ranking.presentation.dto.RankingSearchRequest;
import com.moyo.backend.domain.github_ranking.presentation.dto.RankingSearchResponse;
import com.moyo.backend.domain.github_ranking.service.RankingService;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RankingController {

	private final RankingService rankingService;

	@GetMapping("/rankings")
	public ResponseEntity<ApiResponse<RankingSearchResponse>> getAllUserRanking(
		@Valid RankingSearchRequest request) {

		RankingSearchResponse allUserRanking = rankingService.getAllUserRanking(request);

		return ResponseEntity.ok(ApiResponse.success(allUserRanking));
	}
}
