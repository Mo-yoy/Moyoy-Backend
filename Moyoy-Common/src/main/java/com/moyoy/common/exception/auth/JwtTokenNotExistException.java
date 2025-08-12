package com.moyoy.common.exception.auth;

import com.moyoy.common.exception.MoyoException;

public class JwtTokenNotExistException extends MoyoException {

	public JwtTokenNotExistException() {
		super(AuthErrorCode.TOKEN_NOT_EXIST);
	}
}
