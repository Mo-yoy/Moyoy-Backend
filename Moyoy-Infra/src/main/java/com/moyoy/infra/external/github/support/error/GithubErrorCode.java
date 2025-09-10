package com.moyoy.infra.external.github.support.error;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.common.error.BaseErrorCode;
import com.moyoy.common.error.ErrorReason;

@Getter
@AllArgsConstructor
public enum GithubErrorCode implements BaseErrorCode {

	LIMIT_EXCEED(BAD_REQUEST, "GITHUB_400_1", "Github API 호출이 제한 되었습니다. 1시간 뒤 다시 시도해 주세요"),
	GITHUB_RESOURCE_NOT_FOUND(NOT_FOUND, "GITHUB_404_1", "존재하지 않는 깃허브 리소스 입니다.");

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
