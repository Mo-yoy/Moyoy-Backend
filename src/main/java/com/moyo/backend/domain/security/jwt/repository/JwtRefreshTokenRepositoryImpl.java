package com.moyo.backend.domain.security.jwt.repository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.moyo.backend.domain.security.jwt.domain.JwtRefreshToken;

@Repository
@RequiredArgsConstructor
public class JwtRefreshTokenRepositoryImpl implements JwtRefreshTokenRepository {

	private final JwtRefreshTokenJpaRepositoryImpl jwtRefreshTokenJpaRepository;

	@Override
	public void save(JwtRefreshToken jwtRefreshToken) {
		jwtRefreshTokenJpaRepository.save(jwtRefreshToken);
	}

	@Override
	public boolean existByTokenValue(String tokenValue) {

		return jwtRefreshTokenJpaRepository.existsByValue(tokenValue);
	}

	@Override
	public void deleteByTokenValue(String tokenValue) {

		jwtRefreshTokenJpaRepository.deleteByValue(tokenValue);
	}
}
