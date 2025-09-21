package com.moyoy.domain.pr_review;

import java.time.LocalDateTime;

public record PrReviewCreate(
        Long userId,
        String title,
        Position position,
        String prUrl,
        String content,
        LocalDateTime closedAt
) {
}
