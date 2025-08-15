package com.moyoy.domain.support.error.github;

import static com.moyoy.domain.support.error.github.GithubErrorCode.*;

import com.moyoy.domain.support.error.MoyoException;

public class GithubApiLimitExceedException extends MoyoException {
	public GithubApiLimitExceedException() {
		super(LIMIT_EXCEED);
	}
}
