package com.moyoy.domain.support.error.github;

import com.moyoy.domain.support.error.MoyoException;

public class GithubUserNotFoundException extends MoyoException {
	public GithubUserNotFoundException() {
		super(GithubErrorCode.GITHUB_USER_NOT_FOUND);
	}
}
