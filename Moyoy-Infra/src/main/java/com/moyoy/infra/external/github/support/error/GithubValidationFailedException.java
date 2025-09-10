package com.moyoy.infra.external.github.support.error;

import com.moyoy.common.error.MoyoException;

public class GithubValidationFailedException extends MoyoException {
	public GithubValidationFailedException() {
		super(GithubErrorCode.GITHUB_VALIDATION_FAILED);
	}
}
