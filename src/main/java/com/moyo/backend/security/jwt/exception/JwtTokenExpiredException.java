package com.moyo.backend.security.jwt.exception;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenExpiredException extends MoyoException {
    public JwtTokenExpiredException() {
        super(JwtErrorCode.EXPIRED_TOKEN);
    }
}
