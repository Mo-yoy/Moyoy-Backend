package com.moyoy.core.support.error.github;

import static com.moyoy.core.support.error.github.GithubErrorCode.*;

import com.moyoy.core.support.error.MoyoException;

public class GithubApiLimitExceedException extends MoyoException {
	public GithubApiLimitExceedException() {
		super(LIMIT_EXCEED);
	}
}
