package com.moyo.backend.common.exception.user;

import com.moyo.backend.common.exception.MoyoException;

public class UserNotFoundException extends MoyoException {
	public UserNotFoundException() {
		super(UserErrorCode.USER_NOT_FOUND);
	}
}
