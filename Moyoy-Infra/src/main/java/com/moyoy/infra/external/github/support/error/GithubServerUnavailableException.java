package com.moyoy.infra.external.github.support.error;

import com.moyoy.infra.external.github.support.error.code.GithubErrorCode;

import com.moyoy.common.error.MoyoException;

public class GithubServerUnavailableException extends MoyoException {

	public GithubServerUnavailableException() {
		super(GithubErrorCode.GITHUB_SERVER_UNAVAILABLE);
	}
}
