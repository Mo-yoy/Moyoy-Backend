package com.moyoy.core.support.error.follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.core.support.error.BaseErrorCode;
import com.moyoy.core.support.error.ErrorReason;

@Getter
@AllArgsConstructor
public enum FollowErrorCode implements BaseErrorCode {

	;

	private final Integer status;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getErrorReason() {
		return new ErrorReason(status, code, message);
	}
}
