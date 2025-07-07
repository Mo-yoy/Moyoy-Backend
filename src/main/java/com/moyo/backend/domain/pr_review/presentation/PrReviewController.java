package com.moyo.backend.domain.pr_review.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moyo.backend.common.annotation.LoginUserId;
import com.moyo.backend.common.response.ApiResponse;
import com.moyo.backend.domain.pr_review.business.PrReviewService;
import com.moyo.backend.domain.pr_review.business.dto.*;
import com.moyo.backend.domain.pr_review.business.dto.PrReviewContent;
import com.moyo.backend.domain.pr_review.presentation.dto.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PrReviewController {

	private final PrReviewService prReviewService;

	@GetMapping("/pr-review")
	public ResponseEntity<ApiResponse<PrReviewListResponse>> prReviewList(
		@Valid @ModelAttribute PrReviewListRequest prReviewListRequest) {

		// 1. Business 계층에 넘길 dto로 변환.
		PrReviewSearchCriteria criteria = prReviewListRequest.toCriteria();

		// 2. Business 계층 service에 dto 넘기며 결과 List 반환받음.
		PrReviewListResult result = prReviewService.getPrReviewList(criteria);

		// 3. Presentation 계층 응답 dto로 변환.
		PrReviewListResponse response = PrReviewListResponse.from(result);

		// 4. 최종 응답 반환.
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/pr-review/me")
	public ResponseEntity<ApiResponse<PrReviewListResponse>> myPrReviewList(
		@LoginUserId Long userId,
		@Valid @ModelAttribute PrReviewListRequest prReviewListRequest) {

		// 1. Business 계층에 넘길 dto로 변환.
		PrReviewSearchCriteria criteria = prReviewListRequest.toCriteria();

		// 2. Business 계층 service에 dto 넘기며 결과 List 반환받음.
		PrReviewListResult result = prReviewService.getMyPrReviewList(userId, criteria);

		// 3. Presentation 계층 응답 dto로 변환.
		PrReviewListResponse response = PrReviewListResponse.from(result);

		// 4. 최종 응답 반환.
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/pr-review/{pr-reviewId}")
	public ResponseEntity<ApiResponse<PrReviewDetailResponse>> prReviewDetail(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId) {

		PrReviewDetailResult result = prReviewService.getPrReviewDetail(reviewId, userId);

		PrReviewDetailResponse response = PrReviewDetailResponse.from(result);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/pr-review")
	public ResponseEntity<ApiResponse<PrReviewCreateResponse>> create(
		@LoginUserId Long userId,
		@RequestBody PrReviewCreateRequest prReviewCreateRequest) {

		// 1. Business 계층에 넘길 dto로 변환.
		PrReviewContent content = prReviewCreateRequest.toContent();

		// 2. Business 계층 service에 dto 넘기며 결과 dto 반환.
		PrReviewCreateResult result = prReviewService.createPrReview(content, userId);

		// 3. Presentation 계층 응답 dto로 변환.
		PrReviewCreateResponse response = PrReviewCreateResponse.from(result);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	/*@GetMapping("/pr-review/{pr-reviewId}/form")
	public ResponseEntity<ApiResponse<PrReviewUpdateFormResponseDto>> updateForm(
			@LoginUserId Long userId,
			@PathVariable("pr-reviewId") Long reviewId) {

		return ResponseEntity.ok(ApiResponse.success(prReviewService.getUpdateForm(reviewId, userPrincipal.getId())));
	}

	@PatchMapping("/pr-review/{pr-reviewId}")
	public ResponseEntity<ApiResponse<PrReviewUpdateResponseDto>> update(
			@LoginUserId Long userId,
			@PathVariable("pr-reviewId") Long reviewId,
			@RequestBody PrReviewUpdateRequestDto requestDto) {

		return ResponseEntity.ok(ApiResponse.success(prReviewService.updatePrReview(requestDto, reviewId, userPrincipal.getId())));
	}

	@DeleteMapping("/pr-review/{pr-reviewId}")
	public ResponseEntity<ApiResponse<Void>> delete(
			@LoginUserId Long userId,
			@PathVariable("pr-reviewId") Long reviewId) {

		prReviewService.deletePrReview(reviewId, userPrincipal.getId());
		return ResponseEntity.ok(ApiResponse.noContent());
	}*/
}
