package com.moyoy.infra.external.github.support.error;

import static com.moyoy.infra.external.github.support.error.GithubErrorCode.*;

import com.moyoy.common.error.MoyoException;

public class GithubApiLimitExceedException extends MoyoException {
	public GithubApiLimitExceedException() {
		super(LIMIT_EXCEED);
	}
}
