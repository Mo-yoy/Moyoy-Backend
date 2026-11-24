package com.moyoy.domain.pr_review;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.moyoy.domain.pr_review.dto.PrReviewHitCreate;

class PrReviewHitTest {

	private static final LocalDateTime VIEWED_AT = LocalDateTime.of(2026, 11, 24, 14, 35, 12);

	@Test
	@DisplayName("로그인 사용자는 회원 ID로 조회자를 식별한다.")
	void create_loginUser() {
		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(1L, 10L, "127.0.0.1", VIEWED_AT));

		assertThat(hit.getPrReviewId()).isEqualTo(1L);
		assertThat(hit.getViewerKey()).isEqualTo("user::10");
	}

	@Test
	@DisplayName("익명 사용자는 IP로 조회자를 식별한다.")
	void create_anonymousUser() {
		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(1L, null, "127.0.0.1", VIEWED_AT));

		assertThat(hit.getPrReviewId()).isEqualTo(1L);
		assertThat(hit.getViewerKey()).isEqualTo("ip::127.0.0.1");
	}

	@Test
	@DisplayName("조회 시각이 속한 1시간 구간의 시작 시각을 구한다.")
	void getWindowStart() {
		PrReviewHit hit = PrReviewHit.create(new PrReviewHitCreate(1L, 10L, "127.0.0.1", VIEWED_AT));

		assertThat(hit.getWindowStart()).isEqualTo(LocalDateTime.of(2026, 11, 24, 14, 0, 0));
	}
}
