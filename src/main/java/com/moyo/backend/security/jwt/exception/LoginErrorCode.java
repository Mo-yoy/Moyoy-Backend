package com.moyo.backend.security.jwt.exception;

import com.moyo.backend.common.exception.BaseErrorCode;
import com.moyo.backend.common.exception.ErrorReason;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.moyo.backend.common.constant.MoyoConstants.FORBIDDEN;
import static com.moyo.backend.common.constant.MoyoConstants.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum LoginErrorCode implements BaseErrorCode {

    REFRESH_NOT_EXIST(UNAUTHORIZED, "LOGIN_401_1", "쿠키에 리프레시 토큰이 존재 하지 않습니다."),
    TOKEN_TYPE_MISMATCH(UNAUTHORIZED, "LOGIN_401_2", "토큰 타입이 올바르지 않습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "LOGIN_401_3", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "LOGIN_401_4", "만료된 토큰입니다."),
    BLOCKED_TOKEN(UNAUTHORIZED, "LOGIN_401_5", "블랙리스트 처리된 토큰 입니다."),
    UNAUTHORIZED_USER(UNAUTHORIZED, "LOGIN_401_6", "인증에 실패한 사용자입니다."),
    ACCESS_DENIED(FORBIDDEN, "LOGIN_401_7", "사용자의 권한이 부족합니다.")
    ;

    private final Integer status;
    private final String code;
    private final String message;

    @Override
    public ErrorReason getErrorReason() {
        return new ErrorReason(status,code,message);
    }
}
