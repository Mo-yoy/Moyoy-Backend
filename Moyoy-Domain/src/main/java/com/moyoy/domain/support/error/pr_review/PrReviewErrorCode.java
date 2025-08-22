package com.moyoy.domain.support.error.pr_review;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.domain.support.error.BaseErrorCode;
import com.moyoy.domain.support.error.ErrorReason;

import static com.moyoy.common.constant.MoyoConstants.*;

@Getter
@AllArgsConstructor
public enum PrReviewErrorCode implements BaseErrorCode {

	PR_REVIEW_NOT_FOUND(NOT_FOUND, "PR_REVIEW_404_1", "존재하지 않는 PR 리뷰 요청글입니다."),
	POSITION_NOT_FOUND(NOT_FOUND, "PR_REVIEW_404_2", "존재하지 않는 직군 태그입니다."),
	STATUS_NOT_FOUND(NOT_FOUND, "PR_REVIEW_404_3", "존재하지 않는 상태값입니다."),
	PR_REVIEW_EDIT_FORBIDDEN(FORBIDDEN, "PR_REVIEW_403_1", "PR 리뷰 요청글을 수정할 권한이 없습니다."),
	PR_REVIEW_DELETE_FORBIDDEN(FORBIDDEN, "PR_REVIEW_403_2", "PR 리뷰 요청글을 삭제할 권한이 없습니다."),
	PR_REVIEW_DELETE_CONFLICT(CONFLICT, "PR_REVIEW_409_1", "종료된 PR 리뷰 요청글은 삭제할 수 없습니다.");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
