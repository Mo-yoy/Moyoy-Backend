package com.moyoy.domain.pr_review;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

class PrReviewHitTest {

	@Test
	@DisplayName("lastIncreasedAt이 now면 조회수 증가할 수 있다.")
	void canIncrease_sameTime() {
		LocalDateTime now = LocalDateTime.now();

		PrReviewHit hit = PrReviewHit.create(
			new PrReviewHitCreate(1L, 10L, now));

		assertThat(hit.canIncrease(now)).isTrue();
	}

	@Test
	@DisplayName("TTL 1시간 이내이면 중복 증가로 판단한다.")
	void canIncrease_withinTTL() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime last = now.minusMinutes(30);

		PrReviewHit hit = PrReviewHit.create(
			new PrReviewHitCreate(1L, 10L, last));

		assertThat(hit.canIncrease(now)).isFalse();
	}

	@Test
	@DisplayName("TTL 1시간 범위를 초과했다면 증가 가능하다.")
	void canIncrease_afterTTL() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime last = now.minusHours(1).minusSeconds(1);

		PrReviewHit hit = PrReviewHit.create(
			new PrReviewHitCreate(1L, 10L, last));

		assertThat(hit.canIncrease(now)).isTrue();
	}
}