package com.moyo.backend.domain.pr_review.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.moyo.backend.common.response.ApiResponse;
import com.moyo.backend.domain.pr_review.application.PrReviewService;
import com.moyo.backend.domain.pr_review.dto.request.PrReviewCreateRequestDto;
import com.moyo.backend.domain.pr_review.dto.request.PrReviewListRequestDto;
import com.moyo.backend.domain.pr_review.dto.request.PrReviewUpdateRequestDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewCreateResponseDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewDetailResponseDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewListResponseDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewUpdateFormResponseDto;
import com.moyo.backend.domain.pr_review.dto.response.PrReviewUpdateResponseDto;
import com.moyo.backend.domain.security.oauth.GithubOAuth2User;
import com.moyo.backend.pr_review.dto.response.*;

@Slf4j
@RestController
@RequestMapping("/pr-review")
@RequiredArgsConstructor
public class PrReviewController {

	private final PrReviewService prReviewService;

	@GetMapping
	public ResponseEntity<ApiResponse<PrReviewListResponseDto>> prReviewList(
		@RequestParam(value = "status", defaultValue = "open") String status,
		@RequestParam(value = "order", defaultValue = "createdAt,desc") String order,
		@RequestParam(value = "position", required = false) String position,
		@RequestParam(value = "page") int page,
		@RequestParam(value = "size") int size) {

		// Dto에서 입력값 검증 추가 필요.
		PrReviewListRequestDto requestDto = new PrReviewListRequestDto(status, order, position, page, size);

		return ResponseEntity.ok(ApiResponse.success(prReviewService.getPrReviewList(requestDto)));
	}

	@GetMapping("/{pr-reviewId}")
	public ResponseEntity<ApiResponse<PrReviewDetailResponseDto>> prReviewDetail(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
		@PathVariable("pr-reviewId") Long reviewId) {

		return ResponseEntity.ok(ApiResponse.success(prReviewService.getPrReviewDetail(reviewId, userPrincipal.getId())));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<PrReviewCreateResponseDto>> create(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
		@RequestBody PrReviewCreateRequestDto requestDto) {

		return ResponseEntity.ok(ApiResponse.success(prReviewService.createPrReview(requestDto, userPrincipal.getId())));
	}

	@GetMapping("/{pr-reviewId}/form")
	public ResponseEntity<ApiResponse<PrReviewUpdateFormResponseDto>> updateForm(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
		@PathVariable("pr-reviewId") Long reviewId) {

		return ResponseEntity.ok(ApiResponse.success(prReviewService.getUpdateForm(reviewId, userPrincipal.getId())));
	}

	@PatchMapping("/{pr-reviewId}")
	public ResponseEntity<ApiResponse<PrReviewUpdateResponseDto>> update(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
		@PathVariable("pr-reviewId") Long reviewId,
		@RequestBody PrReviewUpdateRequestDto requestDto) {

		return ResponseEntity.ok(ApiResponse.success(prReviewService.updatePrReview(requestDto, reviewId, userPrincipal.getId())));
	}

	@DeleteMapping("/{pr-reviewId}")
	public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
		@PathVariable("pr-reviewId") Long reviewId) {

		prReviewService.deletePrReview(reviewId, userPrincipal.getId());
		return ResponseEntity.ok(ApiResponse.noContent());
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<PrReviewListResponseDto>> myPrReviewList(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
		@RequestParam(value = "status", defaultValue = "open") String status,
		@RequestParam(value = "order", defaultValue = "createdAt,desc") String order,
		@RequestParam(value = "position", required = false) String position,
		@RequestParam(value = "page") int page,
		@RequestParam(value = "size") int size) {

		// Dto에서 입력값 검증 추가 필요.
		PrReviewListRequestDto requestDto = new PrReviewListRequestDto(status, order, position, page, size);

		return ResponseEntity.ok(ApiResponse.success(prReviewService.getMyPrReviewList(requestDto, userPrincipal.getId())));
	}
}
