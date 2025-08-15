package com.moyoy.domain.support.error.auth;

import com.moyoy.domain.support.error.MoyoException;

public class JwtTokenBlockedException extends MoyoException {
	public JwtTokenBlockedException() {
		super(AuthErrorCode.BLOCKED_TOKEN);
	}
}
