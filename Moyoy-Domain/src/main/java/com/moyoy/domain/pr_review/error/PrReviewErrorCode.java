package com.moyoy.domain.pr_review.error;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.common.error.BaseErrorCode;
import com.moyoy.common.error.ErrorReason;

@Getter
@AllArgsConstructor
public enum PrReviewErrorCode implements BaseErrorCode {

	// 400: 잘못된 요청 파라미터
	INVALID_STATUS(BAD_REQUEST, "PR_REVIEW_400_1", "유효하지 않은 상태값입니다."),
	INVALID_ORDER(BAD_REQUEST, "PR_REVIEW_400_2", "유효하지 않은 정렬 기준입니다."),
	INVALID_POSITION(BAD_REQUEST, "PR_REVIEW_400_3", "유효하지 않은 직군 태그입니다."),

	// 403: 권한 없음
	PR_REVIEW_EDIT_FORBIDDEN(FORBIDDEN, "PR_REVIEW_403_1", "PR 리뷰 요청글을 수정할 권한이 없습니다."),
	PR_REVIEW_DELETE_FORBIDDEN(FORBIDDEN, "PR_REVIEW_403_2", "PR 리뷰 요청글을 삭제할 권한이 없습니다."),

	// 404: 리소스 없음
	PR_REVIEW_NOT_FOUND(NOT_FOUND, "PR_REVIEW_404_1", "존재하지 않는 PR 리뷰 요청글입니다."),

	// 409: 상태 충돌
	PR_REVIEW_DELETE_CONFLICT(CONFLICT, "PR_REVIEW_409_1", "종료된 PR 리뷰 요청글은 삭제할 수 없습니다.");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
