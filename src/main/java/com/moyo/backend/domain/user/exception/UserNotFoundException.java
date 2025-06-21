package com.moyo.backend.domain.user.exception;

import com.moyo.backend.common.exception.MoyoException;

public class UserNotFoundException extends MoyoException {
	public UserNotFoundException() {
		super(UserErrorCode.USER_NOT_FOUNT);
	}
}
