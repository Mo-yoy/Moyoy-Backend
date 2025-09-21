package com.moyoy.api.auth.jwt.support;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
@RequiredArgsConstructor
public class JwtPayloadExtractor {

	private final JwtDecoder jwtDecoder;

	public JwtUserInfo extractUserInfo(String rawToken) {

		SignedJWT signedJWT = jwtDecoder.decode(rawToken);
		JWTClaimsSet jwtClaimsSet = jwtDecoder.extractClaims(signedJWT);

		Long userId = (Long)jwtClaimsSet.getClaim(JWT_CLAIM_USER_ID);
		String authority = jwtClaimsSet.getClaim(JWT_CLAIM_AUTHORITY).toString();

		return new JwtUserInfo(userId, authority);
	}

	public LocalDateTime extractExpirationTime(String rawToken) {

		SignedJWT signedJWT = jwtDecoder.decode(rawToken);
		JWTClaimsSet jwtClaimsSet = jwtDecoder.extractClaims(signedJWT);

		return jwtClaimsSet.getExpirationTime()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
	}
}
