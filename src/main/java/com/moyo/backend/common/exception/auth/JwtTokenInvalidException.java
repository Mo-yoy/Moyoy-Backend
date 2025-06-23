package com.moyo.backend.common.exception.auth;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenInvalidException extends MoyoException {
	public JwtTokenInvalidException() {
		super(AuthErrorCode.INVALID_TOKEN);
	}
}
