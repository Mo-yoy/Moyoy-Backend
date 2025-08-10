package com.moyoy.domain.auth.implement;

import java.text.ParseException;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.moyoy.domain.auth.data_access.JwtRefreshTokenRepository;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

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
