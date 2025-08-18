package com.moyoy.api.auth.jwt.support;

import java.text.ParseException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.moyoy.domain.support.error.auth.JwtTokenInvalidException;

@Slf4j
@Component
public class JwtDecoder {

	public SignedJWT decode(String rawToken) {
		try {
			return SignedJWT.parse(rawToken);
		} catch (ParseException e) {
			log.warn("JWT Parsing 에러", e);
			throw new JwtTokenInvalidException();
		}
	}

	public JWTClaimsSet extractClaims(SignedJWT jwt) {
		try {
			return jwt.getJWTClaimsSet();
		} catch (ParseException e) {
			log.warn("JWT Claim Parsing 에러", e);
			throw new JwtTokenInvalidException();
		}
	}
}
