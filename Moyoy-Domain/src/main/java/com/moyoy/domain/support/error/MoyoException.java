package com.moyoy.domain.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MoyoException extends RuntimeException {

	private final BaseErrorCode errorCode;

	public ErrorReason getErrorReason() {
		return errorCode.getErrorReason();
	}
}
