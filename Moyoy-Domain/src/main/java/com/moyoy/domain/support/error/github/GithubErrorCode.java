package com.moyoy.domain.support.error.github;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.domain.support.error.BaseErrorCode;
import com.moyoy.domain.support.error.ErrorReason;

@Getter
@AllArgsConstructor
public enum GithubErrorCode implements BaseErrorCode {

	LIMIT_EXCEED(BAD_REQUEST, "GITHUB_400_1", "Github API 호출이 제한 되었습니다. 1시간 뒤 다시 시도해 주세요"),
	NOT_ALLOWED_GITHUB_ACCOUNT_TYPE(BAD_REQUEST, "GITHUB_400_2", "지원되지 않는 GitHub 계정 유형입니다. 개인 계정(User)만 사용 가능합니다.");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
