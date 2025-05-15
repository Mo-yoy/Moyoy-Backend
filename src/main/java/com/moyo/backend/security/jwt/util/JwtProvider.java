package com.moyo.backend.security.jwt.util;

import com.moyo.backend.security.oauth.GithubOAuth2User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;

import static com.moyo.backend.common.constant.MoyoConstants.*;

@RequiredArgsConstructor
public class JwtProvider {

    public static final long ONE_MINUTE = 60 * 1000;
    public static final long JWT_ACCESS_TOKEN_EXPIRATION_MINUTE = ONE_MINUTE * 10;
    public static final long JWT_REFRESH_TOKEN_EXPIRATION_MINUTE = ONE_MINUTE * 300;

    private final MACSigner macSigner;

    public String createJwtToken(GithubOAuth2User user, JWK jwk, String type) throws JOSEException {

        Date expirationTime;

        if(type.equals(JWT_REFRESH_TYPE)) expirationTime = new Date(new Date().getTime() + JWT_REFRESH_TOKEN_EXPIRATION_MINUTE);
        else if(type.equals(JWT_ACCESS_TYPE)) expirationTime = new Date(new Date().getTime() + JWT_ACCESS_TOKEN_EXPIRATION_MINUTE);
        else throw new JOSEException();

        return createJwtTokenInternal(macSigner, user, jwk, type, expirationTime);
    }

    private String createJwtTokenInternal(MACSigner macSigner, GithubOAuth2User user, JWK jwk, String type, Date expirationTime) throws JOSEException {

        JWSHeader header = new JWSHeader.Builder((JWSAlgorithm) jwk.getAlgorithm())
                .keyID(jwk.getKeyID())
                .build();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim(JWT_CLAIM_USER_ID, user.getId())
                .claim(JWT_CLAIM_TOKEN_TYPE, type)
                .claim(JWT_CLAIM_AUTHORITY, user.getAuthorities().stream()
                                                .map(GrantedAuthority::getAuthority)
                                                .findFirst().orElseThrow(()->new RuntimeException("권한 부여 실패")))
                .expirationTime(expirationTime)
                .build();

        // header + claim 으로 JWT 생성, 사인 하지 않으면 유효 하지 않은 JWT
        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(macSigner);

        return signedJWT.serialize();
    }
}