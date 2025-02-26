package com.moyo.backend.common.exception;

import lombok.RequiredArgsConstructor;

import static com.moyo.backend.common.constant.MoyoConstants.SERVER_ERROR;


@RequiredArgsConstructor
public enum CommonErrorCode implements BaseErrorCode{

    // 예시
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
