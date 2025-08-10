package com.moyoy.domain.auth.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moyoy.domain.auth.implement.JwtRefreshToken;

public interface JwtRefreshTokenJpaRepository extends JpaRepository<JwtRefreshToken, Long> {

	boolean existsByValue(String tokenValue);

	void deleteByValue(String tokenValue);
}
