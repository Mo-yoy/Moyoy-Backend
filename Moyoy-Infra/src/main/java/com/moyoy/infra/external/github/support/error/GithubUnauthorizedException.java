package com.moyoy.infra.external.github.support.error;

import static com.moyoy.infra.external.github.support.error.code.GithubErrorCode.*;

import com.moyoy.common.error.MoyoException;

public class GithubUnauthorizedException extends MoyoException {
	public GithubUnauthorizedException() {
		super(GITHUB_UNAUTHORIZED);
	}
}
