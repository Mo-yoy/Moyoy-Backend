package com.moyoy.domain.pr_review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PrReview {
	private Long id;
	private Author author;
	private String title;
	private String content;
	private String prUrl;
	private Position position;
	private int hitCount;
	private Status status; // FIXME: open, closed 여부. boolean 변환 하지않고 그냥 Enum으로 관리하는게 좋을듯. 성능 < 유지보수
	private boolean adopted;
	private LocalDateTime deadline; // FIXME: 일주일씩 추가 가능하도록 관리 필요. createdAt도 따로 추가해야 하나 싶음. DateTimeFormmater

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
			null
		);
	}

	public void updateDetail(PrReviewCreate content) {
		this.title = content.title();
		this.content = content.content();
		this.prUrl = content.prUrl();
		this.position = content.position();
	}
}
