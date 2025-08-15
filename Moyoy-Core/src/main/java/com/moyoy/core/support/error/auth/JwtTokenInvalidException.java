package com.moyoy.core.support.error.auth;

import com.moyoy.core.support.error.MoyoException;

public class JwtTokenInvalidException extends MoyoException {
	public JwtTokenInvalidException() {
		super(AuthErrorCode.INVALID_TOKEN);
	}
}
