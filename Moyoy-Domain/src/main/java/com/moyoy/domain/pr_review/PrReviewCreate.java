package com.moyoy.domain.pr_review;

public record PrReviewCreate(
        Long userId,
        String title,
        Position position,
        String prUrl,
        String content
//        String deadline // TODO: 날짜는 받을 떄 LocalDateTime으로 받을 수 있나? 변환해야하겠지?
) {
}
