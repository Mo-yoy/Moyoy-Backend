package com.moyoy.infra.external.github.support.error;

import com.moyoy.common.error.MoyoException;

// 권한 부족 Or API Limit 초과
public class GithubForbiddenException extends MoyoException {
	public GithubForbiddenException() {
		super(GithubErrorCode.GITHUB_FORBIDDEN);
	}
}
