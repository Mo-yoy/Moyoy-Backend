package com.moyo.backend.common.exception.auth;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenNotExistException extends MoyoException {

	public JwtTokenNotExistException() {
		super(AuthErrorCode.TOKEN_NOT_EXIST);
	}
}
