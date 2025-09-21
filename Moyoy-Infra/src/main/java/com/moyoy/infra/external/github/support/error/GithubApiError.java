package com.moyoy.infra.external.github.support.error;

import com.moyoy.common.error.BaseErrorCode;
import com.moyoy.common.error.MoyoException;

public class GithubApiError extends MoyoException {
	public GithubApiError(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
