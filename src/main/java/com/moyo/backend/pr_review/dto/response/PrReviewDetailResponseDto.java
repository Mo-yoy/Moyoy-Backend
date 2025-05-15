package com.moyo.backend.pr_review.dto.response;

public record PrReviewDetailResponseDto(
        String status,
        Boolean isWriter,
//        Boolean isAdopted, // 채택은 스프린트2에서 추가.
        String profileImageUrl,
        String username,
        String position,
        String title,
        Integer hitCount,
        String createdAt,
        String content,
        String prUrl
) {

}