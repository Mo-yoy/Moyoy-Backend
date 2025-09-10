package com.moyoy.infra.external.github.support.error;

import static com.moyoy.infra.external.github.support.error.code.GithubErrorCode.*;

import com.moyoy.common.error.MoyoException;

public class GithubPreCheckLimitExceedException extends MoyoException {
	public GithubPreCheckLimitExceedException() {
		super(GITHUB_LIMIT_PRE_CHECK_EXCEED);
	}
}
