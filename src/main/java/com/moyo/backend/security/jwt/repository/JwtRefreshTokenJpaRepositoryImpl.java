package com.moyo.backend.security.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyo.backend.security.jwt.domain.JwtRefreshToken;

public interface JwtRefreshTokenJpaRepositoryImpl extends JpaRepository<JwtRefreshToken, Long> {

	boolean existsByValue(String tokenValue);

	void deleteByValue(String tokenValue);
}
