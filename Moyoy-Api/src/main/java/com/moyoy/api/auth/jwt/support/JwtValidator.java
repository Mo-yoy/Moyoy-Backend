package com.moyoy.api.auth.jwt.support;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.text.ParseException;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.moyoy.domain.support.error.auth.JwtTokenBlockedException;
import com.moyoy.domain.support.error.auth.JwtTokenExpiredException;
import com.moyoy.domain.support.error.auth.JwtTokenInvalidException;
import com.moyoy.domain.support.error.auth.JwtTokenNotExistException;
import com.moyoy.domain.support.error.auth.JwtTokenTypeMismatchException;
import com.moyoy.infra.database.jwt.JwtRefreshTokenRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

	private final JwtDecoder jwtDecoder;
	private final MACVerifier macVerifier;
	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

	public void validate(JwtType tokenType, String rawToken) {

		switch (tokenType) {
			case JwtType.REFRESH -> validateRefresh(rawToken);
			case JwtType.ACCESS -> validateAccess(rawToken);
		}
	}

	private void validateRefresh(String rawToken){
		validateJwt(JwtType.REFRESH, rawToken);
		validateTokenExistsInWhiteList(rawToken);
	}

	private void validateAccess(String rawToken){
		validateJwt(JwtType.ACCESS, rawToken);
	}

	private void validateJwt(JwtType tokenType, String rawToken){
		validateTokenNotExist(rawToken);

		SignedJWT signedJWT = jwtDecoder.decode(rawToken);
		validateTokenSignature(signedJWT);

		JWTClaimsSet claimsSet = jwtDecoder.extractClaims(signedJWT);
		validateTokenType(tokenType, claimsSet);
		validateTokenExpiration(claimsSet);
	}

	private void validateTokenNotExist(String rawToken) {

		if (rawToken.isBlank()) throw new JwtTokenNotExistException();
	}

	private void validateTokenSignature(SignedJWT signedJWT) {

		boolean verifyResult;

		try {
			verifyResult = signedJWT.verify(macVerifier);
		} catch (JOSEException e) {
			log.warn("JWT 서명 검증 에러");
			throw new JwtTokenInvalidException();
		}

		if (!verifyResult) {
			throw new JwtTokenInvalidException();
		}
	}

	private void validateTokenType(JwtType tokenType, JWTClaimsSet claimsSet) {

		String claimTokenType = claimsSet.getClaim(JWT_CLAIM_TOKEN_TYPE).toString();

		if (claimTokenType == null || !claimTokenType.equals(tokenType.getValue())) {
			log.warn("JWT 토큰 타입 불일치 에러");
			throw new JwtTokenTypeMismatchException();
		}
	}

	private void validateTokenExpiration(JWTClaimsSet claimsSet) {
		Date exp = claimsSet.getExpirationTime();

		if (exp == null || exp.before(new Date())) {
			log.warn("JWT 토큰 유효기간 만료");
			throw new JwtTokenExpiredException();
		}
	}

	private void validateTokenExistsInWhiteList(String rawToken) {

		String tokenHash = HashUtil.sha256Base64(rawToken);

		if (!jwtRefreshTokenRepository.existByTokenHash(tokenHash)) {
			log.warn("차단된 JWT Refresh 토큰");
			throw new JwtTokenBlockedException();
		}
	}

}
