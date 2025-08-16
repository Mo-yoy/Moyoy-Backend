package com.moyoy.api.auth.jwt.legacy;

import java.text.ParseException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jwt.SignedJWT;

@Component
@RequiredArgsConstructor
public class JwtRefreshWhiteListUpdater {

	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

	@Transactional
	public void updateRefreshTokenWhiteList(String oldToken, String newToken) {

		JwtRefreshToken reissuedRefreshToken = null;
		try {
			reissuedRefreshToken = JwtRefreshToken.from(SignedJWT.parse(newToken).getJWTClaimsSet(), newToken);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		/// TODO : 해당 부분에서 알수 없는 오류 발생, 원인을 못 밝혔음
		jwtRefreshTokenRepository.deleteByTokenValue(oldToken);
		jwtRefreshTokenRepository.flush();
		jwtRefreshTokenRepository.save(reissuedRefreshToken);
	}
}
