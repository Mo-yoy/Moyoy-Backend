package com.moyoy.domain.support.error.auth;

import com.moyoy.domain.support.error.MoyoException;

public class JwtTokenInvalidException extends MoyoException {
	public JwtTokenInvalidException() {
		super(AuthErrorCode.INVALID_TOKEN);
	}
}
