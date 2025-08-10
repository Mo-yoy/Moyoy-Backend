package com.moyoy.common.exception;

import lombok.*;

@Getter
@AllArgsConstructor
public class ErrorReason {

	private final Integer status;
	private final String code;
	private String errorMessage;

	public void addDetailErrorMessage(String detailMessage) {

		errorMessage += detailMessage;
	}
}
