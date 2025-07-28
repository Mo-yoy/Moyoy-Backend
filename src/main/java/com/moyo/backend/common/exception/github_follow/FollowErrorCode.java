package com.moyo.backend.common.exception.github_follow;

import static com.moyo.backend.common.constant.MoyoConstants.BAD_REQUEST;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyo.backend.common.exception.BaseErrorCode;
import com.moyo.backend.common.exception.ErrorReason;

@Getter
@AllArgsConstructor
public enum  FollowErrorCode implements BaseErrorCode {

	LIMIT_EXCEED(BAD_REQUEST, "FOLLOW_400_1", "Github API 호출이 제한 되었습니다. 1시간 뒤 다시 시도해 주세요");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
