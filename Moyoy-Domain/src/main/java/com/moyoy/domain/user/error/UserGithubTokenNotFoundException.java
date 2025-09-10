package com.moyoy.domain.user.error;

import com.moyoy.common.error.MoyoException;

public class UserGithubTokenNotFoundException extends MoyoException {
	public UserGithubTokenNotFoundException() {
		super(UserErrorCode.USER_GITHUB_TOKEN_NOT_FOUND);
	}
}
