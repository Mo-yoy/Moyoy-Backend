package com.moyo.backend.security.jwt.exception;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenNotExistException extends MoyoException {

    public JwtTokenNotExistException() {
        super(AuthErrorCode.TOKEN_NOT_EXIST);
    }
}
