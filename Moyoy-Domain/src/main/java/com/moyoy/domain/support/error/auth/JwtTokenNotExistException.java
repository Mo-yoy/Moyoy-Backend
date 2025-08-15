package com.moyoy.domain.support.error.auth;

import com.moyoy.domain.support.error.MoyoException;

public class JwtTokenNotExistException extends MoyoException {

	public JwtTokenNotExistException() {
		super(AuthErrorCode.TOKEN_NOT_EXIST);
	}
}
