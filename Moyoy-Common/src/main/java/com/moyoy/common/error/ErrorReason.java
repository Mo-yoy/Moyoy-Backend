package com.moyoy.common.error;

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
