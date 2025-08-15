package com.moyoy.core.support.error.auth;

import com.moyoy.core.support.error.MoyoException;

public class JwtTokenBlockedException extends MoyoException {
	public JwtTokenBlockedException() {
		super(AuthErrorCode.BLOCKED_TOKEN);
	}
}
