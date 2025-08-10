package com.moyoy.common.exception.pr_review;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.common.exception.BaseErrorCode;
import com.moyoy.common.exception.ErrorReason;

@Getter
@AllArgsConstructor
public enum PrReviewErrorCode implements BaseErrorCode {

	PR_REVIEW_NOT_FOUND(NOT_FOUND, "PR_REVIEW_404_1", "존재하지 않는 PR 리뷰 요청글입니다."),
	POSITION_TAG_NOT_FOUND(NOT_FOUND, "PR_REVIEW_404_2", "존재하지 않는 직군 태그입니다."),
	PR_REVIEW_EDIT_FORBIDDEN(FORBIDDEN, "PR_REVIEW_403_1", "PR 리뷰 요청글을 수정할 권한이 없습니다."),
	PR_REVIEW_DELETE_FORBIDDEN(FORBIDDEN, "PR_REVIEW_403_2", "PR 리뷰 요청글을 삭제할 권한이 없습니다.");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
