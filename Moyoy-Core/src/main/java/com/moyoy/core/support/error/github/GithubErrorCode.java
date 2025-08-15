package com.moyoy.core.support.error.github;

import static com.moyoy.common.constant.MoyoConstants.*;

import com.moyoy.core.support.error.BaseErrorCode;
import com.moyoy.core.support.error.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GithubErrorCode implements BaseErrorCode {

	LIMIT_EXCEED(BAD_REQUEST, "GITHUB_400_1", "Github API 호출이 제한 되었습니다. 1시간 뒤 다시 시도해 주세요");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
