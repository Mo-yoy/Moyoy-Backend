package com.moyo.backend.common.exception.auth;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenExpiredException extends MoyoException {
	public JwtTokenExpiredException() {
		super(AuthErrorCode.EXPIRED_TOKEN);
	}
}
