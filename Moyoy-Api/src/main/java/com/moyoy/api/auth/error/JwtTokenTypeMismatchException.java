package com.moyoy.api.auth.error;

import com.moyoy.common.error.MoyoException;

public class JwtTokenTypeMismatchException extends MoyoException {
	public JwtTokenTypeMismatchException() {
		super(AuthErrorCode.TOKEN_TYPE_MISMATCH);
	}
}
