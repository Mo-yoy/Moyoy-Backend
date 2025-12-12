package com.moyoy.domain.pr_review;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PrReviewTest {

	@Nested
	@DisplayName("PR 리뷰 요청글 마감")
	class ClosePrReview {

		@Test
		void OPEN_상태의_요청글을_마감한다() {

			// given
			LocalDateTime eventTime = LocalDateTime.of(2025, 11, 13, 15, 34);

			PrReview prReview = PrReview.builder()
				.id(1L)
				.userId(10L)
				.title("곧 마감될 글")
				.position(Position.BACKEND)
				.prUrl("https://github.com/Mo-yoy/Moyoy-Backend/pull/888")
				.content("본문은 10자를 넘어야 하니깐")
				.hitCount(10)
				.status(Status.OPEN)
				.adopted(false)
				.createdAt(LocalDateTime.of(2025, 11, 6, 15, 40))
				.closedAt(LocalDateTime.of(2025, 11, 13, 15, 40))
				.build();

			// when
			prReview.close(eventTime);

			// then
			assertThat(prReview.getStatus()).isEqualTo(Status.CLOSED);
			assertThat(prReview.getClosedAt()).isEqualTo(eventTime);
		}

		@Test
		@DisplayName("수동 마감이 경합으로 자동 마감보다 늦게 처리됐을 때, 수동 마감 요청 시간을 우선한다.")
		void CLOSED_상태에서_더_이른_시각의_요청이_오면_그_시간으로_갱신한다() {

			// given
			LocalDateTime originalClosedAt = LocalDateTime.of(2025, 11, 13, 15, 40);
			LocalDateTime eventTime = LocalDateTime.of(2025, 11, 13, 15, 34);

			PrReview prReview = PrReview.builder()
				.id(2L)
				.userId(10L)
				.title("자동 마감 처리된 글")
				.position(Position.BACKEND)
				.prUrl("https://github.com/Mo-yoy/Moyoy-Backend/pull/777")
				.content("아니 마감되기 전에 수동마감 눌렀는데, 작업이 밀려서 자동마감 처리 됐다구욧")
				.hitCount(20)
				.status(Status.CLOSED)
				.adopted(false)
				.createdAt(LocalDateTime.of(2025, 11, 5, 11, 20))
				.closedAt(originalClosedAt)
				.build();

			// when
			prReview.close(eventTime);

			// then
			assertThat(prReview.getStatus()).isEqualTo(Status.CLOSED);
			assertThat(prReview.getClosedAt()).isEqualTo(eventTime);
		}

		@Test
		@DisplayName("이미 마감된 글에 수동 마감을 시도하면, 아무 작업도 안 한다.")
		void CLOSED_상태에서_더_늦은_시각의_요청이_오면_마감_시각을_유지한다() {

			// given
			LocalDateTime originalClosedAt = LocalDateTime.of(2025, 11, 13, 15, 34); // 자동마감이 먼저 처리한 시각
			LocalDateTime eventTime = LocalDateTime.of(2025, 11, 13, 15, 40); // 수동마감 시도.

			PrReview prReview = PrReview.builder()
				.id(3L)
				.userId(10L)
				.title("이미 마감된 글")
				.position(Position.BACKEND)
				.prUrl("https://github.com/Mo-yoy/Moyoy-Backend/pull/666")
				.content("원래는 예외를 터뜨려야 하나 했는데, 나중에 자동마감 배치에서 예외 터지면 곤란해서 그냥 nothing to do")
				.hitCount(5)
				.status(Status.CLOSED)
				.adopted(false)
				.createdAt(LocalDateTime.of(2025, 11, 6, 15, 34))
				.closedAt(originalClosedAt)
				.build();

			// when
			prReview.close(eventTime);

			// then
			assertThat(prReview.getStatus()).isEqualTo(Status.CLOSED);
			assertThat(prReview.getClosedAt()).isEqualTo(originalClosedAt);
		}
	}
}
