package com.moyoy.api.auth.jwt.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyoy.api.auth.jwt.implement.JwtRefreshToken;

public interface JwtRefreshTokenJpaRepository extends JpaRepository<JwtRefreshToken, Long> {

	boolean existsByValue(String tokenValue);

	void deleteByValue(String tokenValue);
}
