package com.moyoy.api.auth.error;

import com.moyoy.common.error.MoyoException;

public class JwtTokenNotExistException extends MoyoException {

	public JwtTokenNotExistException() {
		super(AuthErrorCode.TOKEN_NOT_EXIST);
	}
}
