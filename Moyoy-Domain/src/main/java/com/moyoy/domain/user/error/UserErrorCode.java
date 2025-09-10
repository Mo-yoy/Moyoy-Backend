package com.moyoy.domain.user.error;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.moyoy.common.error.BaseErrorCode;
import com.moyoy.common.error.ErrorReason;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

	NOT_ALLOWED_GITHUB_ACCOUNT_TYPE(BAD_REQUEST, "USER_400_1", "지원되지 않는 GitHub 계정 유형입니다. 개인 계정(User)만 사용 가능합니다."),
	USER_NOT_FOUND(NOT_FOUND, "USER_404_1", "존재하지 않는 사용자 입니다."),
	USER_GITHUB_TOKEN_NOT_FOUND(NOT_FOUND, "USER_404_2", "사용자의 깃허브 토큰이 존재하지 않습니다.");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
