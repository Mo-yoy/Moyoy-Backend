package com.moyoy.domain.user.error;

import com.moyoy.common.error.MoyoException;

public class UserNotFoundException extends MoyoException {
	public UserNotFoundException() {
		super(UserErrorCode.USER_NOT_FOUND);
	}
}
