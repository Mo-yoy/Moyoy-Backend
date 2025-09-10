package com.moyoy.api.auth.error;

import com.moyoy.common.error.MoyoException;

public class JwtTokenInvalidException extends MoyoException {
	public JwtTokenInvalidException() {
		super(AuthErrorCode.INVALID_TOKEN);
	}
}
