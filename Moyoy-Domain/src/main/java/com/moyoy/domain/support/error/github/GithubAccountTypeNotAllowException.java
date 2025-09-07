package com.moyoy.domain.support.error.github;

import com.moyoy.domain.support.error.MoyoException;

public class GithubAccountTypeNotAllowException extends MoyoException {
	public GithubAccountTypeNotAllowException() {
		super(GithubErrorCode.NOT_ALLOWED_GITHUB_ACCOUNT_TYPE);
	}
}
