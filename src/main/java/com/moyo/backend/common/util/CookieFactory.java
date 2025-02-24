package com.moyo.backend.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static com.moyo.backend.common.constant.MoyoConstants.REFRESH_TYPE;

@Component
public class CookieFactory {

    private final String domain;

    public CookieFactory(@Value("${spring.cookie.domain}") String domain) {
        this.domain = domain;
    }

    public ResponseCookie createJwtRefreshCookie(String jwtRefresh) {
        return ResponseCookie.from(REFRESH_TYPE, jwtRefresh)
                .path("/")
                .maxAge(300)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .domain(domain)
                .build();
    }
}