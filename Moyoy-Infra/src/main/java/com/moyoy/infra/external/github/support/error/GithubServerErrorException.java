package com.moyoy.infra.external.github.support.error;

import com.moyoy.common.error.ErrorReason;
import com.moyoy.common.error.MoyoException;

public class GithubServerErrorException extends MoyoException {
	public GithubServerErrorException(int status) {
		super(() -> new ErrorReason(
			status,
			"GITHUB_SERVER_ERROR",
			"GitHub SERVER Error (status=" + status + ")"
		));
	}
}
