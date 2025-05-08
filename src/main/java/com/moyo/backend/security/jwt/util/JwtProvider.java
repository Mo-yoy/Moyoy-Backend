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

import java.util.Date;

import static com.moyo.backend.common.constant.MoyoConstants.JWT_REFRESH_TYPE;

@RequiredArgsConstructor
public class JwtProvider {

    private static final long JWT_ACCESS_TOKEN_EXPIRATION = 60 * 1000 * 10;
    private static final long JWT_REFRESH_TOKEN_EXPIRATION = 60 * 1000 * 600;

    private final MACSigner macSigner;

    public String createJwtToken(GithubOAuth2User user, JWK jwk, String type) throws JOSEException {

        Date expirationTime;

        if(type.equals(JWT_REFRESH_TYPE)) expirationTime = new Date(new Date().getTime() + JWT_REFRESH_TOKEN_EXPIRATION);
        else expirationTime = new Date(new Date().getTime() + JWT_ACCESS_TOKEN_EXPIRATION);

        return createJwtTokenInternal(macSigner, user, jwk, type, expirationTime);
    }

    private String createJwtTokenInternal(MACSigner macSigner, GithubOAuth2User user, JWK jwk, String type, Date expirationTime) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder((JWSAlgorithm) jwk.getAlgorithm())
                                        .keyID(jwk.getKeyID()).build();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("id", user.getId())
                .claim("type", type)
                .claim("authority", user.getAuthorities())
                .expirationTime(expirationTime)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(macSigner);

        return signedJWT.serialize();
    }
}