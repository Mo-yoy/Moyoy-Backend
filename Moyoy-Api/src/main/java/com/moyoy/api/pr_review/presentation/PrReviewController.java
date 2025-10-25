package com.moyoy.api.pr_review.presentation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moyoy.api.auth.security.annotation.LoginUserId;
import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.api.pr_review.application.PrReviewService;
import com.moyoy.api.pr_review.application.request.PrReviewCreateData;
import com.moyoy.api.pr_review.application.request.PrReviewUpdateData;
import com.moyoy.api.pr_review.application.request.SearchConditionData;
import com.moyoy.api.pr_review.application.response.*;
import com.moyoy.api.pr_review.presentation.request.PrReviewCreateRequest;
import com.moyoy.api.pr_review.presentation.request.PrReviewListRequest;
import com.moyoy.api.pr_review.presentation.request.PrReviewUpdateRequest;
import com.moyoy.api.pr_review.presentation.response.PrReviewDetailResponse;
import com.moyoy.api.pr_review.presentation.response.PrReviewListResponse;
import com.moyoy.api.pr_review.presentation.response.PrReviewRedirectResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PrReviewController {

	private final PrReviewService prReviewService;

	@GetMapping("/pr-reviews")
	public ResponseEntity<ApiResponse<PrReviewListResponse>> getPrReviewList(
		@Valid @ModelAttribute PrReviewListRequest request) {

		SearchConditionData data = request.toSearchCondition();
		PrReviewListResult result = prReviewService.getPrReviewList(data);

		PrReviewListResponse response = PrReviewListResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/pr-reviews/me")
	public ResponseEntity<ApiResponse<PrReviewListResponse>> getMyPrReviewList(
		@LoginUserId Long userId,
		@Valid @ModelAttribute PrReviewListRequest request) {

		SearchConditionData data = request.toSearchCondition(userId);
		PrReviewListResult result = prReviewService.getPrReviewList(data);

		PrReviewListResponse response = PrReviewListResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/pr-reviews/{pr-reviewId}")
	public ResponseEntity<ApiResponse<PrReviewDetailResponse>> getPrReviewDetail(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId) {

		PrReviewDetailResult result = prReviewService.getPrReviewDetail(reviewId, userId);

		PrReviewDetailResponse response = PrReviewDetailResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/pr-reviews")
	public ResponseEntity<ApiResponse<PrReviewRedirectResponse>> create(
		@LoginUserId Long userId,
		@Valid @RequestBody PrReviewCreateRequest request) {

		PrReviewCreateData data = request.toData();
		PrReviewCreateResult result = prReviewService.createPrReview(data, userId);

		PrReviewRedirectResponse response = PrReviewRedirectResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PatchMapping("/pr-reviews/{pr-reviewId}")
	public ResponseEntity<ApiResponse<PrReviewRedirectResponse>> update(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId,
		@Valid @RequestBody PrReviewUpdateRequest request) {

		PrReviewUpdateData data = request.toData();
		PrReviewUpdateResult result = prReviewService.updatePrReview(reviewId, data, userId);

		PrReviewRedirectResponse response = PrReviewRedirectResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@DeleteMapping("/pr-reviews/{pr-reviewId}")
	public ResponseEntity<ApiResponse<Void>> delete(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId) {

		prReviewService.deletePrReview(reviewId, userId);

		return ResponseEntity.ok(ApiResponse.noContent());
	}
}
