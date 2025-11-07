package com.moyoy.domain.pr_review;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.moyoy.domain.pr_review.error.InvalidStatusException;

@Getter
@RequiredArgsConstructor
public enum Status {
	OPEN("OPEN"),
	CLOSED("CLOSED");

	private final String value;

	public static Status from(String value) {
		return Arrays.stream(Status.values())
			.filter(status -> status.name().equalsIgnoreCase(value) || status.value.equals(value))
			.findFirst()
			.orElseThrow(InvalidStatusException::new);
	}

	public boolean isClosed() {
		return this == CLOSED;
	}
}
