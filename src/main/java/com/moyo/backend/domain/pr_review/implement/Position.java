package com.moyo.backend.domain.pr_review.implement;

import java.util.Arrays;

import com.moyo.backend.common.exception.CommonErrorCode;
import com.moyo.backend.common.exception.MoyoException;
import com.moyo.backend.common.exception.pr_review.PrReviewErrorCode;

public enum Position {
	BACKEND("백엔드"),
	FRONTEND("프론트엔드"),
	IOS("iOS"),
	ANDROID("안드로이드"),
	DEVOPS("DevOps"),
	OTHER("기타");

	private final String value;

	Position(String value) {
		this.value = value;
	}

	public static Position from(String value) {

		if (value == null)
			return null; // 직군 태그 null 허용할 건지, 다음 회의 안건 등록. FIXME

		return Arrays.stream(Position.values())
			.filter(position -> position.name().equalsIgnoreCase(value) || position.value.equals(value))
			.findFirst()
			.orElseThrow(() -> new MoyoException(PrReviewErrorCode.POSITION_TAG_NOT_FOUND));
	}
}
