package com.moyo.backend.security.jwt.exception;

import com.moyo.backend.common.exception.BaseErrorCode;
import com.moyo.backend.common.exception.ErrorReason;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.moyo.backend.common.constant.MoyoConstants.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum LoginErrorCode implements BaseErrorCode {
    
    // 임시
    JWT_EXPIRED(UNAUTHORIZED, "LOGIN_401_1", "JWT 만료")
    ;

    private final Integer status;
    private final String code;
    private final String message;

    @Override
    public ErrorReason getErrorReason() {
        return new ErrorReason(status,code,message);
    }
}
