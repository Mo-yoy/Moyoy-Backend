package com.moyoy.core.support.error.user;

import com.moyoy.core.support.error.MoyoException;

public class UserNotFoundException extends MoyoException {
	public UserNotFoundException() {
		super(UserErrorCode.USER_NOT_FOUND);
	}
}
