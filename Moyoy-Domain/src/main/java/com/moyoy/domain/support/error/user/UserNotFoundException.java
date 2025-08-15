package com.moyoy.domain.support.error.user;

import com.moyoy.domain.support.error.MoyoException;

public class UserNotFoundException extends MoyoException {
	public UserNotFoundException() {
		super(UserErrorCode.USER_NOT_FOUND);
	}
}
