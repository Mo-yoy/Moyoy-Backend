package com.moyoy.domain.support.error.github;

import com.moyoy.domain.support.error.MoyoException;

public class GithubResourceNotFoundException extends MoyoException {
	public GithubResourceNotFoundException() {
		super(GithubErrorCode.GITHUB_RESOURCE_NOT_FOUND);
	}
}
