package com.moyoy.infra.external.github.support.error;

import com.moyoy.common.error.ErrorReason;

public class GithubUnknownErrorException extends GithubApiError {
	public GithubUnknownErrorException(int status) {
		super(() -> new ErrorReason(
			status,
			"GITHUB_UNKNOWN_ERROR",
			"GitHub UNKNOWN Error (status=" + status + ")"));
	}

	public GithubUnknownErrorException(int status, Throwable cause) {
		super(() -> new ErrorReason(
			status,
			"GITHUB_UNKNOWN_ERROR",
			"GitHub UNKNOWN Error (status=" + status + ", cause=" + cause.getMessage() + ")"));
	}
}
