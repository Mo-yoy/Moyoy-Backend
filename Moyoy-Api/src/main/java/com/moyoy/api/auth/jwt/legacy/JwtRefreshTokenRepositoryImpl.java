package com.moyoy.api.auth.jwt.legacy;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JwtRefreshTokenRepositoryImpl implements JwtRefreshTokenRepository {

	private final JwtRefreshTokenJpaRepository jwtRefreshTokenJpaRepository;

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

	@Override
	public void flush() {
		jwtRefreshTokenJpaRepository.flush();
	}
}
