package com.moyo.backend.user.exception;

import com.moyo.backend.common.constant.MoyoConstants;
import com.moyo.backend.common.exception.BaseErrorCode;
import com.moyo.backend.common.exception.ErrorReason;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.moyo.backend.common.constant.MoyoConstants.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    USER_NOT_FOUNT(BAD_REQUEST, "USER_400_1", "존재하지 않는 사용자 입니다.")
    ;

    private final Integer status;
    private final String code;
    private final String message;

    @Override
    public ErrorReason getErrorReason() {
        return new ErrorReason(status,code,message);
    }
}
