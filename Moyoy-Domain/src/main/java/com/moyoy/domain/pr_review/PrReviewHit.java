package com.moyoy.domain.pr_review;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

@Getter
@Builder
public class PrReviewHit {
	private Long id;
	private Long prReviewId;
	private Long userId;
	private LocalDateTime lastIncreasedAt;

	private static final Duration TTL = Duration.ofHours(1);

	public static PrReviewHit create(PrReviewHitCreate dto) {
		return new PrReviewHit(
			null,
			dto.prReviewId(),
			dto.userId(),
			dto.lastIncreasedAt());
	}

	public boolean canIncrease(LocalDateTime now) {
		if (lastIncreasedAt.equals(now)) {
			return true;
		}

		return lastIncreasedAt.plus(TTL).isBefore(now);
	}
}
