package com.moyoy.api.pr_review.presentation.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.moyoy.api.pr_review.application.request.PrReviewCreateData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PrReviewCreateRequest(
	@NotBlank(message = "제목은 필수입니다.") @Size(min = 5, max = 20, message = "제목은 5~20자여야 합니다.") String title,

	String position,

	@NotBlank(message = "PR URL은 필수입니다.") @Pattern(regexp = "^https://github\\.com/[^/]+/[^/]+/pull/\\d+(?:/.*)?$", message = "유효한 GitHub PR URL을 입력해 주세요.") String prUrl,

	@NotBlank(message = "내용은 필수입니다.") @Size(min = 10, message = "내용은 10자 이상이어야 합니다.") String content,

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime closedAt) {

	public PrReviewCreateData toData() {
		return PrReviewCreateData.of(title, position, prUrl, content, closedAt);
	}
}
