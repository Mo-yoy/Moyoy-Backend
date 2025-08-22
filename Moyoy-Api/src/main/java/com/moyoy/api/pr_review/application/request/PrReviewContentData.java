package com.moyoy.api.pr_review.application.request;


import com.moyoy.domain.pr_review.Position;
import com.moyoy.domain.pr_review.PrReviewCreate;

public record PrReviewContentData(
	String title,
	Position position,
	String prUrl,
	String content) {

	public static PrReviewContentData of(String title, String position, String prUrl, String content) {
		return new PrReviewContentData(
			title,
			Position.from(position),
			prUrl,
			content
		);
	}

//	public PrReviewCreate toCreateContent() {
//		return new PrReviewCreate(title, position, prUrl, content);
//	}
//	public static PrReviewContent create(PrReviewFormRequest request) {
//		return new PrReviewContent(
//			request.title(),
//			request.position(),
//			request.prUrl(),
//			request.content());
//	}
}
