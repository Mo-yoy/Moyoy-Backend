package com.moyo.backend.domain.security.jwt.exception;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenInvalidException extends MoyoException {
	public JwtTokenInvalidException() {
		super(AuthErrorCode.INVALID_TOKEN);
	}
}
