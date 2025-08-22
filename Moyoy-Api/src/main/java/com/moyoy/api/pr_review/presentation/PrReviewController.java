package com.moyoy.api.pr_review.presentation;

import com.moyoy.api.common.annotation.LoginUserId;
import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.api.pr_review.application.request.PrReviewContentData;
import com.moyoy.api.pr_review.application.request.SearchCondition;
import com.moyoy.api.pr_review.application.response.*;
import com.moyoy.api.pr_review.presentation.request.PrReviewFormRequest;
import com.moyoy.api.pr_review.presentation.request.PrReviewListRequest;
import com.moyoy.api.pr_review.presentation.response.PrReviewDetailResponse;
import com.moyoy.api.pr_review.presentation.response.PrReviewListResponse;
import com.moyoy.api.pr_review.presentation.response.PrReviewRedirectResponse;
import com.moyoy.api.pr_review.presentation.response.PrReviewUpdateFormResponse;
import com.moyoy.api.pr_review.application.PrReviewService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PrReviewController {

	private final PrReviewService prReviewService;

	///  TODO pr-review -> pr-reviews
	
	@GetMapping("/pr-review")
	public ResponseEntity<ApiResponse<PrReviewListResponse>> getPrReviewList(
		@Valid @ModelAttribute PrReviewListRequest request) {

		///  TODO : XXXDATA <-> RESULT
		SearchCondition condition = request.toSearchCondition();
		PrReviewListResult result = prReviewService.getPrReviewList(condition);

		PrReviewListResponse response = PrReviewListResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/pr-review/me")
	public ResponseEntity<ApiResponse<PrReviewListResponse>> getMyPrReviewList(
		@LoginUserId Long userId,
		@Valid @ModelAttribute PrReviewListRequest request) {

		SearchCondition condition = request.toSearchCondition();
		PrReviewListResult result = prReviewService.getMyPrReviewList(userId, condition);

		PrReviewListResponse response = PrReviewListResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/pr-review/{pr-reviewId}")
	public ResponseEntity<ApiResponse<PrReviewDetailResponse>> getPrReviewDetail(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId) {

		PrReviewDetailResult result = prReviewService.getPrReviewDetail(reviewId, userId);

		PrReviewDetailResponse response = PrReviewDetailResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping("/pr-review")
	public ResponseEntity<ApiResponse<PrReviewRedirectResponse>> create(
		@LoginUserId Long userId,
		@RequestBody PrReviewFormRequest request) {

		PrReviewContentData content = request.toContent();
		PrReviewCreateResult result = prReviewService.createPrReview(content, userId);

		PrReviewRedirectResponse response = PrReviewRedirectResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping("/pr-review/{pr-reviewId}/form")
	public ResponseEntity<ApiResponse<PrReviewUpdateFormResponse>> updateForm(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId) {

		PrReviewContentResult result = prReviewService.getPrReviewUpdateForm(reviewId, userId);

		PrReviewUpdateFormResponse response = PrReviewUpdateFormResponse.from(result);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PatchMapping("/pr-review/{pr-reviewId}")
	public ResponseEntity<ApiResponse<PrReviewRedirectResponse>> update(
		@LoginUserId Long userId,
		@PathVariable("pr-reviewId") Long reviewId,
		@RequestBody PrReviewFormRequest request) {

		PrReviewContentData content = request.toContent();
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
