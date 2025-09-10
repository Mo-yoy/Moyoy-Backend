package com.moyoy.domain.user.error;

import com.moyoy.common.error.MoyoException;

public class UserGithubAccountTypeNotAllowException extends MoyoException {
	public UserGithubAccountTypeNotAllowException() {
		super(UserErrorCode.NOT_ALLOWED_GITHUB_ACCOUNT_TYPE);
	}
}
