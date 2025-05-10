package com.moyo.backend.security.jwt.exception;

import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenNotExistException extends MoyoException {

    public JwtTokenNotExistException() {
        super(LoginErrorCode.TOKEN_NOT_EXIST);
    }
}
