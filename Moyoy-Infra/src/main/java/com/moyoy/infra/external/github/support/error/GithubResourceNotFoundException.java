package com.moyoy.infra.external.github.support.error;

import static com.moyoy.infra.external.github.support.error.code.GithubErrorCode.*;

import com.moyoy.common.error.MoyoException;

public class GithubResourceNotFoundException extends MoyoException {
	public GithubResourceNotFoundException() {
		super(GITHUB_RESOURCE_NOT_FOUND);
	}
}
