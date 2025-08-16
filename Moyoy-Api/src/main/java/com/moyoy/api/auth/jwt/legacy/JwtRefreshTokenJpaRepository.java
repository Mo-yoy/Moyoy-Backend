package com.moyoy.api.auth.jwt.legacy;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtRefreshTokenJpaRepository extends JpaRepository<JwtRefreshToken, Long> {

	boolean existsByValue(String tokenValue);

	void deleteByValue(String tokenValue);
}
