package com.moyo.backend.domain.auth.jwt.implement;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

	public String createJwtToken(JwtUserInfo jwtUserInfo, String tokenType) {

		Date expirationTime = getTokenExpiration(tokenType);

		JWSHeader header = createJWSHeader();
		JWTClaimsSet payload = createJWSPayload(jwtUserInfo, tokenType, expirationTime);
		SignedJWT signedJWT = new SignedJWT(header, payload);

		signJwt(signedJWT);

		return signedJWT.serialize();
	}

	private Date getTokenExpiration(String tokenType) {

		if (tokenType.equals(JWT_REFRESH_TYPE))
			return new Date(new Date().getTime() + JWT_REFRESH_TOKEN_EXPIRATION_MINUTE);
		else if (tokenType.equals(JWT_ACCESS_TYPE))
			return new Date(new Date().getTime() + JWT_ACCESS_TOKEN_EXPIRATION_MINUTE);
		else
			throw new RuntimeException();
	}

	private JWSHeader createJWSHeader() {

		return new JWSHeader.Builder((JWSAlgorithm)jwk.getAlgorithm())
			.keyID(jwk.getKeyID())
			.build();
	}

	private JWTClaimsSet createJWSPayload(JwtUserInfo jwtUserInfo, String tokenType, Date expirationTime) {

		return new JWTClaimsSet.Builder()
			.claim(JWT_CLAIM_USER_ID, jwtUserInfo.userId())
			.claim(JWT_CLAIM_TOKEN_TYPE, tokenType)
			.claim(JWT_CLAIM_AUTHORITY, jwtUserInfo.authority())
			.expirationTime(expirationTime)
			.build();
	}

	private void signJwt(SignedJWT signedJWT) {

		try {
			signedJWT.sign(macSigner);
		} catch (JOSEException e) {
			log.error("JWT Refresh Token 사인 중, 알 수 없는 이유로 사인에 실패했습니다.", e);
			throw new RuntimeException("Jwt 발급 중 에러 발생");
		}
	}

}
