package com.moyoy.domain.pr_review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PrReview {
	private Long id;
	private Author author; // User id를 넣어두고 query repository로 조회해서 반환받을 것.
	private String title;
	private String content;
	private String prUrl;
	private Position position;
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
			Author.createInitial(prReviewCreate.userId()), // Author 객체 생성
			prReviewCreate.title(),
			prReviewCreate.content(),
			prReviewCreate.prUrl(),
			prReviewCreate.position(),
			0,
			Status.OPEN,
			false,
			null,
			prReviewCreate.closedAt()
		);
	}

	public void updateDetail(PrReviewCreate content) {
		this.title = content.title();
		this.content = content.content();
		this.prUrl = content.prUrl();
		this.position = content.position();
	}
}
