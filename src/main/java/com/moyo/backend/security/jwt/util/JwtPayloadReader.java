package com.moyo.backend.security.jwt.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.moyo.backend.common.constant.MoyoConstants.*;

@Component
public class JwtPayloadReader {

    private final SecretKey secretKey;

    public JwtPayloadReader(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getTokenType(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get(JWT_PAYLOAD_TOKEN_TYPE, String.class);
    }

    public String getProviderId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get(JWT_PAYLOAD_PROVIDER_ID, String.class);
    }

    public String getRole(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get(JWT_PAYLOAD_ROLE, String.class);
    }

    public Date getExpiration(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }


}
