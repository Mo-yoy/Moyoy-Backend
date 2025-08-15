package com.moyoy.core.support.error.auth;

import com.moyoy.core.support.error.MoyoException;

public class JwtTokenTypeMismatchException extends MoyoException {
	public JwtTokenTypeMismatchException() {
		super(AuthErrorCode.TOKEN_TYPE_MISMATCH);
	}
}
