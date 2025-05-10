package com.moyo.backend.security.jwt.exception;

import com.moyo.backend.common.exception.MoyoException;

public class JwtRefreshTokenNotExistException extends MoyoException {

    public JwtRefreshTokenNotExistException() {
        super(LoginErrorCode.REFRESH_NOT_EXIST);
    }
}
