package com.moyoy.domain.pr_review;

import com.moyoy.domain.support.error.pr_review.PositionNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

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
			return null; // 직군 태그 null 허용할 건지, 다음 회의 안건 등록. FIXME

		return Arrays.stream(Position.values())
			.filter(position -> position.name().equalsIgnoreCase(value) || position.value.equals(value))
			.findFirst()
			.orElseThrow(PositionNotFoundException::new);
	}
}
