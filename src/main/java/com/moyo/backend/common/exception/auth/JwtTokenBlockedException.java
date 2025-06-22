package com.moyo.backend.common.exception.auth;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenBlockedException extends MoyoException {
	public JwtTokenBlockedException() {
		super(AuthErrorCode.BLOCKED_TOKEN);
	}
}
