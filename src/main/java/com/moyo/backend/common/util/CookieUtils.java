package com.moyo.backend.common.util;

import org.springframework.http.ResponseCookie;

import static com.moyo.backend.common.constant.MoyoConstants.JWT_PAYLOAD_REFRESH_TYPE;

public class CookieUtils {

    public static ResponseCookie createJwtRefreshCookie(String jwtRefresh, long maxAgeSeconds) {
        return ResponseCookie.from(JWT_PAYLOAD_REFRESH_TYPE, jwtRefresh)
                .path("/")
                .maxAge(600)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .build();
    }
}