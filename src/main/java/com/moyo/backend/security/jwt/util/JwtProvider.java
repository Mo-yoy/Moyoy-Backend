package com.moyo.backend.security.jwt.util;


import com.moyo.backend.domain.user.Role;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.moyo.backend.common.constant.MoyoConstants.*;

@Component
public class JwtProvider {

    private static final long JWT_ACCESS_EXPIRES_MS = 60 * 1000;
    private static final long JWT_REFRESH_EXPIRES_MS = 60 * 1000 * 5;


    private final SecretKey secretKey;

    public JwtProvider(@Value("${spring.jwt.secret}") String jwtSecret) {
        this.secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createJwtAccess(String providerId) {
        return Jwts.builder()
                .claim(JWT_PAYLOAD_TOKEN_TYPE, JWT_PAYLOAD_ACCESS_TYPE)
                .claim(JWT_PAYLOAD_PROVIDER_ID, providerId)
                .claim(JWT_PAYLOAD_ROLE, Role.USER)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_ACCESS_EXPIRES_MS))
                .signWith(secretKey)
                .compact();
    }

    public String createJwtRefresh(String providerId) {
        return Jwts.builder()
                .claim(JWT_PAYLOAD_TOKEN_TYPE, JWT_PAYLOAD_REFRESH_TYPE)
                .claim(JWT_PAYLOAD_PROVIDER_ID, providerId)
                .claim(JWT_PAYLOAD_ROLE, Role.USER)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_REFRESH_EXPIRES_MS))
                .signWith(secretKey)
                .compact();
    }

}
