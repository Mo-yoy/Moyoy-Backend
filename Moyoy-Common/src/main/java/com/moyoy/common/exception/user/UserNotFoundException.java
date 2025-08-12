package com.moyoy.common.exception.user;

import com.moyoy.common.exception.MoyoException;

public class UserNotFoundException extends MoyoException {
	public UserNotFoundException() {
		super(UserErrorCode.USER_NOT_FOUND);
	}
}
