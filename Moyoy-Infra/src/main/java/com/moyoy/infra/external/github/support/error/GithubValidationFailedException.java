package com.moyoy.infra.external.github.support.error;

import com.moyoy.infra.external.github.support.error.code.GithubErrorCode;

public class GithubValidationFailedException extends GithubApiError {
	public GithubValidationFailedException() {
		super(GithubErrorCode.GITHUB_VALIDATION_FAILED);
	}
}
