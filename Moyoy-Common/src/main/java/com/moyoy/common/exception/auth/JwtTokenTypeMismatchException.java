package com.moyoy.common.exception.auth;

import com.moyoy.common.exception.MoyoException;

public class JwtTokenTypeMismatchException extends MoyoException {
	public JwtTokenTypeMismatchException() {
		super(AuthErrorCode.TOKEN_TYPE_MISMATCH);
	}
}
