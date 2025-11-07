package com.moyoy.domain.pr_review;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.moyoy.domain.pr_review.error.InvalidPositionException;

@Getter
@RequiredArgsConstructor
public enum Position {
	BACKEND("백엔드"),
	FRONTEND("프론트엔드"),
	IOS("iOS"),
	ANDROID("안드로이드"),
	DEVOPS("DevOps"),
	OTHER("기타");

	private final String value;

	public static Position from(String value) {

		if (value == null)
			return null;

		return Arrays.stream(Position.values())
			.filter(position -> position.name().equalsIgnoreCase(value) || position.value.equals(value))
			.findFirst()
			.orElseThrow(InvalidPositionException::new);
	}
}
