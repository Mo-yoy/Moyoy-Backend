package com.moyo.backend.domain.auth.jwt.implement;

import java.text.ParseException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jwt.SignedJWT;

import com.moyo.backend.domain.auth.jwt.data_access.JwtRefreshTokenRepository;

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
		jwtRefreshTokenRepository.deleteByTokenValue(oldToken);
		jwtRefreshTokenRepository.flush();
		jwtRefreshTokenRepository.save(reissuedRefreshToken);
	}
}
