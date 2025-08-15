package com.moyoy.domain.support.error.auth;

import com.moyoy.domain.support.error.MoyoException;

public class JwtTokenTypeMismatchException extends MoyoException {
	public JwtTokenTypeMismatchException() {
		super(AuthErrorCode.TOKEN_TYPE_MISMATCH);
	}
}
