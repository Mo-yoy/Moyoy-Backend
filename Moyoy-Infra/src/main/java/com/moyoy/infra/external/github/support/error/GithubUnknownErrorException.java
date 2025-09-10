package com.moyoy.infra.external.github.support.error;

import com.moyoy.common.error.ErrorReason;
import com.moyoy.common.error.MoyoException;

public class GithubUnknownErrorException extends MoyoException {
	public GithubUnknownErrorException(int status) {
		super(() -> new ErrorReason(
			status,
			"GITHUB_UNKNOWN_ERROR",
			"GitHub UNKNOWN Error (status=" + status + ")"
		));
	}
}
