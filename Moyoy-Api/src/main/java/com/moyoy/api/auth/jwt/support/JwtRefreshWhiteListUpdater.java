package com.moyoy.api.auth.jwt.support;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JwtRefreshWhiteListUpdater {

	private final JwtRotator jwtRotator;
	private final JwtPayloadExtractor jwtPayloadExtractor;

	@Transactional
	public void updateRefreshTokenWhiteList(Long userId, String oldRawToken, String newRawToken) {

		String oldTokenHash = HashUtil.sha256Base64(oldRawToken);
		String newTokenHash = HashUtil.sha256Base64(newRawToken);

		LocalDateTime expirationTime = jwtPayloadExtractor.extractExpirationTime(newRawToken);
		JwtRefreshTokenEntity reissuedRefreshToken = JwtRefreshTokenEntity.of(userId, newTokenHash, expirationTime);

		jwtRotator.rotate(oldTokenHash, reissuedRefreshToken);
	}
}
