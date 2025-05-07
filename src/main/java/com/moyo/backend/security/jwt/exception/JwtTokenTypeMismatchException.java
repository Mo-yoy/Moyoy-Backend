package com.moyo.backend.security.jwt.exception;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenTypeMismatchException extends MoyoException {
    public JwtTokenTypeMismatchException() {
        super(JwtErrorCode.TOKEN_TYPE_MISMATCH);
    }
}
