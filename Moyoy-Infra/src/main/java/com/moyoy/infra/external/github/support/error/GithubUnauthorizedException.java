package com.moyoy.infra.external.github.support.error;

import static com.moyoy.infra.external.github.support.error.code.GithubErrorCode.*;

public class GithubUnauthorizedException extends GithubApiError {
	public GithubUnauthorizedException() {
		super(GITHUB_UNAUTHORIZED);
	}
}
