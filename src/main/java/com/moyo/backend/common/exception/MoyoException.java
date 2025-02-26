package com.moyo.backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MoyoException extends RuntimeException{

    private final BaseErrorCode baseErrorCode;

    public ErrorReason getErrorReason(){
        return baseErrorCode.getErrorReason();
    }
}