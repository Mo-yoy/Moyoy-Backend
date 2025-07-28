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

		PrReviewSearchCriteria criteria = prReviewListRequest.toCriteria();

		PrReviewListResult result = prReviewService.getPrReviewList(criteria);

		PrReviewListResponse response = PrReviewListResponse.from(result);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/pr-review/me")
	public ResponseEntity<ApiResponse<PrReviewListResponse>> myPrReviewList(
		@LoginUserId Long userId,
		@Valid @ModelAttribute PrReviewListRequest prReviewListRequest) {

		PrReviewSearchCriteria criteria = prReviewListRequest.toCriteria();

		PrReviewListResult result = prReviewService.getMyPrReviewList(userId, criteria);

		PrReviewListResponse response = PrReviewListResponse.from(result);

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
	public ResponseEntity<ApiResponse<PrReviewRedirectResponse>> create(
		@LoginUserId Long userId,
		@RequestBody PrReviewFormRequest prReviewFormRequest) {

		PrReviewContent content = prReviewFormRequest.toContent();

		PrReviewCreateResult result = prReviewService.createPrReview(content, userId);

		PrReviewRedirectResponse response = PrReviewRedirectResponse.from(result);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/pr-review/{pr-reviewId}/form")
	public ResponseEntity<ApiResponse<PrReviewUpdateFormResponse>> updateForm(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId) {

		PrReviewContent result = prReviewService.getPrReviewUpdateForm(reviewId, userId);

		PrReviewUpdateFormResponse response = PrReviewUpdateFormResponse.from(result);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PatchMapping("/pr-review/{pr-reviewId}")
	public ResponseEntity<ApiResponse<PrReviewRedirectResponse>> update(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId,
		@RequestBody PrReviewFormRequest prReviewFormRequest) {

		PrReviewContent content = prReviewFormRequest.toContent();

		PrReviewUpdateResult result = prReviewService.updatePrReview(reviewId, content, userId);

		PrReviewRedirectResponse response = PrReviewRedirectResponse.from(result);

		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@DeleteMapping("/pr-review/{pr-reviewId}")
	public ResponseEntity<ApiResponse<Void>> delete(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId) {

		prReviewService.deletePrReview(reviewId, userId);

		return ResponseEntity.ok(ApiResponse.noContent());
	}
}
