package com.moyoy.domain.support.error.auth;

import com.moyoy.domain.support.error.MoyoException;

public class JwtTokenExpiredException extends MoyoException {
	public JwtTokenExpiredException() {
		super(AuthErrorCode.EXPIRED_TOKEN);
	}
}
