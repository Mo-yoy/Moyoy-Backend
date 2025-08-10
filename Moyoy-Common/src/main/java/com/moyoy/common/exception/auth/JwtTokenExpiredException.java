package com.moyoy.common.exception.auth;

import com.moyoy.common.exception.MoyoException;

public class JwtTokenExpiredException extends MoyoException {
	public JwtTokenExpiredException() {
		super(AuthErrorCode.EXPIRED_TOKEN);
	}
}
