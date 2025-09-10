package com.moyoy.infra.external.github.support.error;

import com.moyoy.common.error.ErrorReason;

public class GithubServerErrorException extends GithubApiError {
	public GithubServerErrorException(int status) {
		super(() -> new ErrorReason(
			status,
			"GITHUB_SERVER_ERROR",
			"GitHub SERVER Error (status=" + status + ")"));
	}
}
