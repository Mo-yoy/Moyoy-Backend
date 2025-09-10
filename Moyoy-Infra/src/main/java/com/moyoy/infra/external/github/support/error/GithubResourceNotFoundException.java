package com.moyoy.infra.external.github.support.error;

import com.moyoy.common.error.MoyoException;

public class GithubResourceNotFoundException extends MoyoException {
	public GithubResourceNotFoundException() {
		super(GithubErrorCode.GITHUB_RESOURCE_NOT_FOUND);
	}
}
