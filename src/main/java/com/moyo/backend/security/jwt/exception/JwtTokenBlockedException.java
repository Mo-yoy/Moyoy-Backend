package com.moyo.backend.security.jwt.exception;

import com.moyo.backend.common.exception.BaseErrorCode;
import com.moyo.backend.common.exception.MoyoException;

public class JwtTokenBlockedException extends MoyoException {
    public JwtTokenBlockedException() {
        super(JwtErrorCode.BLOCKED_TOKEN);
    }
}
