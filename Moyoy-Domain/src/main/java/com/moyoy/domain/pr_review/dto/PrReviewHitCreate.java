package com.moyoy.domain.pr_review.dto;

import java.time.LocalDateTime;

public record PrReviewHitCreate(
    Long prReviewId,
    Long userId,
    LocalDateTime lastIncreasedAt
) {
}
