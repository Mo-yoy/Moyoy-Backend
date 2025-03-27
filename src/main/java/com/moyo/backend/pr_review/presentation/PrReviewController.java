package com.moyo.backend.pr_review.presentation;

import com.moyo.backend.common.model.ApiResponse;
import com.moyo.backend.pr_review.dto.request.PrReviewCreateRequestDto;
import com.moyo.backend.pr_review.dto.request.PrReviewListRequestDto;
import com.moyo.backend.pr_review.dto.request.PrReviewUpdateRequestDto;
import com.moyo.backend.pr_review.dto.response.*;
import com.moyo.backend.pr_review.service.PrReviewService;
import com.moyo.backend.security.oauth.dto.GithubOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/pr-review")
@RequiredArgsConstructor
public class PrReviewController {

    private final PrReviewService prReviewService;

    // 요청글 전체 조회.
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

    // 요청글 상세 조회.
    @GetMapping("/{pr-reviewId}")
    public ResponseEntity<ApiResponse<PrReviewDetailResponseDto>> prReviewDetail(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
                                                                                 @PathVariable("pr-reviewId") Long reviewId) {

        log.info("현재 요청을 보낸 사용자 provider Id: {}", userPrincipal.getName());

        return ResponseEntity.ok(ApiResponse.success(prReviewService.getPrReviewDetail(reviewId, userPrincipal.getId())));
    }

    // 요청 글 생성.
    @PostMapping
    public ResponseEntity<ApiResponse<PrReviewCreateResponseDto>> create(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
                                                                         @RequestBody PrReviewCreateRequestDto requestDto) {

        return ResponseEntity.ok(ApiResponse.success(prReviewService.createPrReview(requestDto, userPrincipal.getId())));
    }

    // 요청 글 수정 폼.
    @GetMapping("/{pr-reviewId}/form")
    public ResponseEntity<ApiResponse<PrReviewUpdateFormResponseDto>> updateForm(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
                                                                                    @PathVariable("pr-reviewId") Long reviewId) {

        return ResponseEntity.ok(ApiResponse.success(prReviewService.getUpdateForm(reviewId, userPrincipal.getId())));
    }

    // 요청 글 수정.
    @PatchMapping("/{pr-reviewId}")
    public ResponseEntity<ApiResponse<PrReviewUpdateResponseDto>> update(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
                                                                         @PathVariable("pr-reviewId") Long reviewId,
                                                                         @RequestBody PrReviewUpdateRequestDto requestDto) {

        return ResponseEntity.ok(ApiResponse.success(prReviewService.updatePrReview(requestDto, reviewId, userPrincipal.getId())));
    }

    // 요청 글 삭제.
    @DeleteMapping("/{pr-reviewId}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal GithubOAuth2User userPrincipal,
                                    @PathVariable("pr-reviewId") Long reviewId) {

        prReviewService.deletePrReview(reviewId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    // 내 리뷰 요청글 모아보기
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