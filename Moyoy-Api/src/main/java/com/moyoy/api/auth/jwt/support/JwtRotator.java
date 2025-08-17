package com.moyoy.api.auth.jwt.support;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRotator {

	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;

	@Transactional
	public void rotate(String oldTokenHash, JwtRefreshTokenEntity reissuedRefreshToken){

		jwtRefreshTokenRepository.deleteByTokenHash(oldTokenHash);
		jwtRefreshTokenRepository.save(reissuedRefreshToken);
	}
}
