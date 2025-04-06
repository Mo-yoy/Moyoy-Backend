package com.moyo.backend.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static com.moyo.backend.common.constant.MoyoConstants.ACCESS_TYPE;
import static com.moyo.backend.common.constant.MoyoConstants.REFRESH_TYPE;

@Component
public class CookieFactory {

    private final String domain;
    private final String samesite;

    public CookieFactory(@Value("${spring.cookie.domain}") String domain,
                         @Value("${spring.cookie.samesite}") String samesite) {
        this.domain = domain;
        this.samesite = samesite;
    }

    public ResponseCookie createJwtRefreshCookie(String jwtRefresh) {
        return ResponseCookie.from(REFRESH_TYPE, jwtRefresh)
                .path("/")
                .maxAge(6000)
                .httpOnly(true)
                .secure(true)
                .sameSite(samesite)
                .domain(domain)
                .build();
    }

}