package com.moyoy.api.presentation.v1.domain.pr_review.request;

import com.moyoy.core.domain.pr_review.business.dto.PrReviewContent;

public record PrReviewFormRequest(
	String title,
	String position,
	String prUrl,
	String content) {
	public PrReviewFormRequest { // 글자수 제한이나 형식 제한 검증할 것인지.
		title = (title == null) ? "" : title;
		position = (position == null) ? "" : position;
		prUrl = (prUrl == null) ? "" : prUrl;
		content = (content == null) ? "" : content;
	}

	public PrReviewContent toContent() {
		return new PrReviewContent(title, position, prUrl, content);
	}
}
