package com.moyo.backend.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Base64;
import java.util.Optional;

import static com.moyo.backend.common.constant.MoyoConstants.JWT_REFRESH_TYPE;

@Component
public class CookieUtils {

    private final String domain;
    private final String samesite;

    public CookieUtils(@Value("${spring.cookie.domain}") String domain,
                       @Value("${spring.cookie.samesite}") String samesite) {
        this.domain = domain;
        this.samesite = samesite;
    }

    public ResponseCookie createJwtRefreshTokenCookie(String jwtRefresh) {
        return ResponseCookie.from(JWT_REFRESH_TYPE, jwtRefresh)
                .path("/")
                .maxAge(6000)
                .httpOnly(true)
                .secure(true)
                .sameSite(samesite)
                .domain(domain)
                .build();
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value) {

        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .maxAge(1800)
                .sameSite("Strict")
                .secure(false)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }


    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize((Serializable) object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }


}