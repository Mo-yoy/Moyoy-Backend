package com.moyoy.api.pr_review.presentation.request;

import com.moyoy.api.pr_review.application.request.PrReviewContentData;

import java.time.LocalDateTime;

public record PrReviewFormRequest(
	String title,
	String position,
	String prUrl,
	String content,
	LocalDateTime deadline) { // FIXME: 마감일 추가 작업해야 함.
	public PrReviewFormRequest { // FIXME: 글자수 제한이나 형식 제한 검증할 것인가?
		title = (title == null) ? "요청글 기본 제목" : title;
		prUrl = (prUrl == null) ? "" : prUrl;
		content = (content == null) ? "입력된 내용이 없습니다." : content;
	}

	public PrReviewContentData toContent() {
		return PrReviewContentData.of(title, position, prUrl, content);
	}
}
