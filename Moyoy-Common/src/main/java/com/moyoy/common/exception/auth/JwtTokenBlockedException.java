package com.moyoy.common.exception.auth;

import com.moyoy.common.exception.MoyoException;

public class JwtTokenBlockedException extends MoyoException {
	public JwtTokenBlockedException() {
		super(AuthErrorCode.BLOCKED_TOKEN);
	}
}
