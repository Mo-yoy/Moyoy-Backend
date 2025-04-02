package com.moyo.backend.pr_review.domain.position;

import com.moyo.backend.common.exception.CommonErrorCode;
import com.moyo.backend.common.exception.MoyoException;

import java.util.Arrays;

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

    public static Position fromString(String value) {
        return Arrays.stream(Position.values())
                .filter(position -> position.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->  new MoyoException(CommonErrorCode.POSITION_TAG_NOT_FOUND));
    }
}
