package com.moyoy.api.auth.jwt.implement;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.text.ParseException;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.moyoy.common.exception.auth.JwtTokenBlockedException;
import com.moyoy.common.exception.auth.JwtTokenExpiredException;
import com.moyoy.common.exception.auth.JwtTokenInvalidException;
import com.moyoy.common.exception.auth.JwtTokenNotExistException;
import com.moyoy.common.exception.auth.JwtTokenTypeMismatchException;
import com.moyoy.domain.auth.data_access.JwtRefreshTokenRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRefreshTokenValidator {

	private final MACVerifier macVerifier;
	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

	public void validate(String jwtRefreshToken) {

		validateTokenNotExist(jwtRefreshToken);

		SignedJWT signedJWT = parseToken(jwtRefreshToken);
		validateTokenSignature(signedJWT);

		JWTClaimsSet claimsSet = getJwtClaimsSet(signedJWT);
		validateTokenType(claimsSet);
		validateTokenExpiration(claimsSet);
		validateTokenExistsInWhiteList(jwtRefreshToken);
	}

	private void validateTokenNotExist(String jwtRefreshToken) {

		if (jwtRefreshToken.isBlank())
			throw new JwtTokenNotExistException();
	}

	private JWTClaimsSet getJwtClaimsSet(SignedJWT signedJWT) {
		JWTClaimsSet claimsSet;

		try {
			claimsSet = signedJWT.getJWTClaimsSet();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		return claimsSet;
	}

	private SignedJWT parseToken(String token) {

		try {

			return SignedJWT.parse(token);
		} catch (ParseException e) {
			log.warn("JWT Parsing 에러");
			throw new JwtTokenInvalidException();
		}

	}

	private void validateTokenSignature(SignedJWT signedJWT) {

		boolean verifyResult = false;

		try {
			verifyResult = signedJWT.verify(macVerifier);
		} catch (JOSEException e) {
			throw new JwtTokenInvalidException();
		}

		if (!verifyResult) {
			throw new JwtTokenInvalidException();
		}
	}

	private void validateTokenType(JWTClaimsSet claimsSet) {

		String tokenType = claimsSet.getClaim(JWT_CLAIM_TOKEN_TYPE).toString();

		if (tokenType != null && !tokenType.equals(JWT_REFRESH_TYPE)) {
			throw new JwtTokenTypeMismatchException();
		}
	}

	private void validateTokenExpiration(JWTClaimsSet claimsSet) {

		if (claimsSet.getExpirationTime() != null && claimsSet.getExpirationTime().before(new Date())) {
			throw new JwtTokenExpiredException();
		}
	}

	private void validateTokenExistsInWhiteList(String token) {

		if (!jwtRefreshTokenRepository.existByTokenValue(token)) {
			throw new JwtTokenBlockedException();
		}
	}

}
