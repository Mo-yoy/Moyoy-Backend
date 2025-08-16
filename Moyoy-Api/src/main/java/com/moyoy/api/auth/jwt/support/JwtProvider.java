package com.moyoy.api.auth.jwt.support;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.moyoy.domain.support.error.auth.JwtTokenInvalidException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

	private final MACSigner macSigner;
	private final JWK jwk;

	public String createJwtToken(JwtUserInfo jwtUserInfo, JwtType tokenType) {

		Date expirationTime = getTokenExpiration(tokenType);

		JWSHeader header = createJWSHeader();
		JWTClaimsSet payload = createJWSPayload(jwtUserInfo, tokenType, expirationTime);
		SignedJWT signedJWT = new SignedJWT(header, payload);

		signJwt(signedJWT);

		return signedJWT.serialize();
	}

	private Date getTokenExpiration(JwtType tokenType) {

		long expirationMillis = switch (tokenType) {
			case JwtType.REFRESH -> JWT_REFRESH_TOKEN_EXPIRATION_MINUTE;
			case JwtType.ACCESS -> JWT_ACCESS_TOKEN_EXPIRATION_MINUTE;
		};

		return new Date(System.currentTimeMillis() + expirationMillis);
	}

	private JWSHeader createJWSHeader() {

		return new JWSHeader.Builder((JWSAlgorithm)jwk.getAlgorithm())
			.keyID(jwk.getKeyID())
			.build();
	}

	private JWTClaimsSet createJWSPayload(JwtUserInfo jwtUserInfo, JwtType tokenType, Date expirationTime) {

		return new JWTClaimsSet.Builder()
			.claim(JWT_CLAIM_USER_ID, jwtUserInfo.userId())
			.claim(JWT_CLAIM_TOKEN_TYPE, tokenType.getValue())
			.claim(JWT_CLAIM_AUTHORITY, jwtUserInfo.authority())
			.expirationTime(expirationTime)
			.build();
	}

	private void signJwt(SignedJWT signedJWT) {

		try {
			signedJWT.sign(macSigner);
		} catch (JOSEException e) {
			log.warn("JWT 서명중 에러 발생");
			throw new JwtTokenInvalidException();
		}
	}

}
