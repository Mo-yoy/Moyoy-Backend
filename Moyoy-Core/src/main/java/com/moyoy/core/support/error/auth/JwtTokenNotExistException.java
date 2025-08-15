package com.moyoy.core.support.error.auth;

import com.moyoy.core.support.error.MoyoException;

public class JwtTokenNotExistException extends MoyoException {

	public JwtTokenNotExistException() {
		super(AuthErrorCode.TOKEN_NOT_EXIST);
	}
}
