package com.moyoy.common.exception.github;

import static com.moyoy.common.exception.github.GithubErrorCode.*;

import com.moyoy.common.exception.MoyoException;

public class GithubApiLimitExceedException extends MoyoException {
	public GithubApiLimitExceedException() {
		super(LIMIT_EXCEED);
	}
}
