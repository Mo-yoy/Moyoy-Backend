package com.moyo.backend.security.jwt;

import com.moyo.backend.security.oauth.GithubOAuth2User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;

import static com.moyo.backend.common.constant.MoyoConstants.JWT_REFRESH_TYPE;

public class MacSecuritySigner {

    public String getJwtToken(GithubOAuth2User user, JWK jwk, String type) throws JOSEException {

        MACSigner jwsSigner = new MACSigner(((OctetSequenceKey)jwk).toSecretKey());

        Date expirationTime;

        if(type.equals(JWT_REFRESH_TYPE)) expirationTime = new Date(new Date().getTime() + 60 * 1000 * 100);
        else expirationTime = new Date(new Date().getTime() + 60 * 1000);

        return getJwtTokenInternal(jwsSigner, user, jwk, type, expirationTime);
    }

    private String getJwtTokenInternal(MACSigner jwsSigner, GithubOAuth2User user, JWK jwk, String type, Date expirationTime) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder((JWSAlgorithm) jwk.getAlgorithm())
                                        .keyID(jwk.getKeyID()).build();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("user")
                .issuer("https://moyoy.com")
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .claim("type", type)
                .claim("authority", user.getAuthorities())
                .expirationTime(expirationTime)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        signedJWT.sign(jwsSigner);

        return signedJWT.serialize();
    }
}