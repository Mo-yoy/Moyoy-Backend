package com.moyoy.infra.external.github.support.error;

import static com.moyoy.infra.external.github.support.error.code.GithubErrorCode.*;

public class GithubPreCheckLimitExceedException extends GithubApiError {
	public GithubPreCheckLimitExceedException() {
		super(GITHUB_LIMIT_PRE_CHECK_EXCEED);
	}
}
