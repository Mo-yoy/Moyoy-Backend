package com.moyo.backend.security.jwt.repository;

import org.springframework.stereotype.Repository;

import com.moyo.backend.security.jwt.domain.JwtRefreshToken;

import lombok.RequiredArgsConstructor;

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
