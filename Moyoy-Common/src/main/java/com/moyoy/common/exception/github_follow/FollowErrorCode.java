package com.moyoy.common.exception.github_follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.common.exception.BaseErrorCode;
import com.moyoy.common.exception.ErrorReason;

@Getter
@AllArgsConstructor
public enum FollowErrorCode implements BaseErrorCode {

	LIMIT_EXCEED(BAD_REQUEST, "FOLLOW_400_1", "Github API 호출이 제한 되었습니다. 1시간 뒤 다시 시도해 주세요");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
