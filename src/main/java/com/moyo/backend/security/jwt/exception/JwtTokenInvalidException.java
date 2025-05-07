package com.moyo.backend.security.jwt.exception;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenInvalidException extends MoyoException {
    public JwtTokenInvalidException() {
        super(JwtErrorCode.INVALID_TOKEN);
    }
}
