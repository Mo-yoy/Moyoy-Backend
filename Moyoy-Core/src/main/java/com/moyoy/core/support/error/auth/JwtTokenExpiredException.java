package com.moyoy.core.support.error.auth;

import com.moyoy.core.support.error.MoyoException;

public class JwtTokenExpiredException extends MoyoException {
	public JwtTokenExpiredException() {
		super(AuthErrorCode.EXPIRED_TOKEN);
	}
}
