package com.moyo.backend.common.exception.auth;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenTypeMismatchException extends MoyoException {
	public JwtTokenTypeMismatchException() {
		super(AuthErrorCode.TOKEN_TYPE_MISMATCH);
	}
}
