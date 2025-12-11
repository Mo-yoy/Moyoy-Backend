package com.moyoy.domain.pr_review;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder
public class PrReviewHit {
    private Long id;
    private Long prReviewId;
    private Long userId;
    private LocalDateTime lastIncreasedAt;

    private static final Duration TTL = Duration.ofHours(1);

    public boolean canIncrease(LocalDateTime now) {
        return lastIncreasedAt.plus(TTL).isBefore(now);
    }
}
