package com.moyo.backend.domain.auth.jwt.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyo.backend.domain.auth.jwt.implement.JwtRefreshToken;

public interface JwtRefreshTokenJpaRepositoryImpl extends JpaRepository<JwtRefreshToken, Long> {

	boolean existsByValue(String tokenValue);

	void deleteByValue(String tokenValue);
}
