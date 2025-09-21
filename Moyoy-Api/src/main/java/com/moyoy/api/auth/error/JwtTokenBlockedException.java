package com.moyoy.api.auth.error;

import com.moyoy.common.error.MoyoException;

public class JwtTokenBlockedException extends MoyoException {
	public JwtTokenBlockedException() {
		super(AuthErrorCode.BLOCKED_TOKEN);
	}
}
