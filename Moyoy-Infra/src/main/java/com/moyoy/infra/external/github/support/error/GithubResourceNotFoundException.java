package com.moyoy.infra.external.github.support.error;

import static com.moyoy.infra.external.github.support.error.code.GithubErrorCode.*;

public class GithubResourceNotFoundException extends GithubApiError {
	public GithubResourceNotFoundException() {
		super(GITHUB_RESOURCE_NOT_FOUND);
	}
}
