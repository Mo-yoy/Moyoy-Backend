package com.moyoy.domain.pr_review;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

import com.moyoy.domain.pr_review.dto.PrReviewCreate;
import com.moyoy.domain.pr_review.dto.PrReviewUpdate;

@Getter
@Builder
public class PrReview {
	private Long id;
	private Long userId;
	private String title;
	private Position position;
	private String prUrl;
	private String content;
	private int hitCount;
	private Status status;
	private boolean adopted;
	private LocalDateTime createdAt;
	private LocalDateTime closedAt;

	public void increaseHitCount() {
		this.hitCount++;
	} // TODO

	public static PrReview create(PrReviewCreate prReviewCreate) {
		return new PrReview(
			null,
			prReviewCreate.userId(),
			prReviewCreate.title(),
			prReviewCreate.position(),
			prReviewCreate.prUrl(),
			prReviewCreate.content(),
			0,
			Status.OPEN,
			false,
			null,
			prReviewCreate.closedAt());
	}

	public void updateDetail(PrReviewUpdate content) {
		this.title = content.title();
		this.position = content.position();
		this.prUrl = content.prUrl();
		this.content = content.content();
		this.closedAt = content.closedAt();
	}

	public void close(LocalDateTime eventTime) {
		if (this.status == Status.CLOSED) {
			if (eventTime.isBefore(this.closedAt)) {
				this.closedAt = eventTime;
			}
			return;
		}

		this.status = Status.CLOSED;
		this.closedAt = eventTime;
	}
}
