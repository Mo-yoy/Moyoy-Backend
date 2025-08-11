package com.moyoy.common.exception.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.moyoy.common.exception.BaseErrorCode;
import com.moyoy.common.exception.ErrorReason;

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
