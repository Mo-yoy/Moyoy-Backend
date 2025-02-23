package com.moyo.backend.security.jwt.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtValidator {

    private final JwtPayloadReader jwtPayloadReader;
    private final SecretKey secretKey;

    public JwtValidator(JwtPayloadReader jwtPayloadReader,
                        @Value("${spring.jwt.secret}") String secret) {

        this.jwtPayloadReader = jwtPayloadReader;
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public void validateJwtAccessToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)   // 시크릿 키 세팅
                    .build()
                    .parseSignedClaims(token); // 토큰 검증

            // tokenType이 access인지 검증
            isAccessToken(token);

        } catch (ExpiredJwtException e){   // ExpiredJwtException -> ClaimJwtException -> JwtException
            throw new RuntimeException("JWT Access 만료 예외 발생");
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 JWT 예외 발생");
        }
    }

    private void isAccessToken(String token){
        if(!jwtPayloadReader.getTokenType(token).equals("access")) throw new RuntimeException("유효하지 않은 JWT 예외 발생");
    }



    public void validateJwtRefreshToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            isRefreshToken(token);

        } catch (ExpiredJwtException e){ // 프론트 쪽에서 다시 로그인 하라는 화면 처리가 필요함
            throw new RuntimeException("액세스 만료");
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 JWT 예외 발생");
        }
    }

    private void isRefreshToken(String token){
        if(!jwtPayloadReader.getTokenType(token).equals("refresh")) throw new RuntimeException("유효하지 않은 JWT 예외 발생");
    }

}
