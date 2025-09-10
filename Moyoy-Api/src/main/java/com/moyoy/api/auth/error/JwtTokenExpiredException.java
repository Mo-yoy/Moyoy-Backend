package com.moyoy.api.auth.error;

import com.moyoy.common.error.MoyoException;

public class JwtTokenExpiredException extends MoyoException {
	public JwtTokenExpiredException() {
		super(AuthErrorCode.EXPIRED_TOKEN);
	}
}
