package com.moyoy.infra.external.github.support.error;

import com.moyoy.common.error.ErrorReason;

public class GithubClientErrorException extends GithubApiError {
	public GithubClientErrorException(int status) {
		super(() -> new ErrorReason(
			status,
			"GITHUB_CLIENT_ERROR",
			"GitHub Client Error (status=" + status + ")"));
	}
}
