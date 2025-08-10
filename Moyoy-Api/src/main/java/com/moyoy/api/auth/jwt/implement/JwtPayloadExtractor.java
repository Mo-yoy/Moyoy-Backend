package com.moyoy.api.auth.jwt.implement;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class JwtPayloadExtractor {

	public JwtUserInfo extractUserInfo(String jwtRefreshToken) {

		JWTClaimsSet jwtClaimsSet = extractClaimsFromToken(jwtRefreshToken);

		Long userId = (Long)jwtClaimsSet.getClaim(JWT_CLAIM_USER_ID);
		String authority = jwtClaimsSet.getClaim(JWT_CLAIM_AUTHORITY).toString();

		return new JwtUserInfo(userId, authority);
	}

	private JWTClaimsSet extractClaimsFromToken(String jwtRefreshToken) {

		try {
			SignedJWT signedJWT = SignedJWT.parse(jwtRefreshToken);
			return signedJWT.getJWTClaimsSet();
		} catch (ParseException e) {
			throw new RuntimeException("JWT 토큰 파싱 중 실패", e);
		}
	}
}
