package com.moyoy.infra.external.github.support.error.code;

import static com.moyoy.common.constant.MoyoConstants.*;

import com.moyoy.common.error.BaseErrorCode;
import com.moyoy.common.error.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GithubErrorCode implements BaseErrorCode {

	GITHUB_LIMIT_PRE_CHECK_EXCEED(BAD_REQUEST, "GITHUB_400_1", "요청이 GitHub API 한도를 초과할 것으로 예상됩니다. 1시간 뒤 다시 시도해 주세요."),
	GITHUB_UNAUTHORIZED(UNAUTHORIZED, "GITHUB_401_1", "GitHub 인증에 실패했습니다. Access Token을 확인해 주세요."),
	GITHUB_FORBIDDEN(FORBIDDEN, "GITHUB_403_1", "권한이 부족하거나 GitHub API Rate Limit이 초과되었습니다."),
	GITHUB_RESOURCE_NOT_FOUND(NOT_FOUND, "GITHUB_404_1", "존재하지 않는 GitHub 리소스입니다."),
	GITHUB_VALIDATION_FAILED(UNPROCESSABLE_ENTITY, "GITHUB_422_1", "GitHub 요청 파라미터 검증에 실패했거나 과한 요청으로 스팸처리되었습니다.")
	;

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
