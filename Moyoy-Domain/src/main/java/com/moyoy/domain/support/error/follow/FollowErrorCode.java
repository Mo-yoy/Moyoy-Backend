package com.moyoy.domain.support.error.follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.domain.support.error.BaseErrorCode;
import com.moyoy.domain.support.error.ErrorReason;

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
