package com.moyo.backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.moyo.backend.common.constant.MoyoConstants.*;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements BaseErrorCode{

    // 예시
    INVALID_PARAM(BAD_REQUEST, "COMMON_400_1", "파라미터를 다시 확인해 주세요."),
    PARAM_TYPE_MISMATCH(BAD_REQUEST, "COMMON_400_2", "파라미터의 타입을 다시 확인해 주세요."),
    RESOURCE_NOT_FOUND(NOT_FOUND, "COMMON_404_1", "리소스를 찾을 수 없습니다."),
    NOT_ALLOWED_METHOD(METHOD_NOT_ALLOWED, "COMMON_405_1", "허용되지 않은 메서드 입니다."),
    UNKNOWN_INTERNAL_SERVER_ERROR(SERVER_ERROR, "COMMON_500_1", "서버 내부에서 알수 없는 에러가 발생 했습니다. 관리자에게 문의해 주세요.")
    ;

    private final Integer status;
    private final String code;
    private final String message;

    @Override
        public ErrorReason getErrorReason() {
        return new ErrorReason(status,code,message);
    }
}
