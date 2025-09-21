package com.moyoy.api.auth.jwt.support;

public interface JwtRefreshTokenRepository {

	void save(JwtRefreshTokenEntity jwtRefreshTokenEntity);

	boolean existByTokenHash(String tokenHash);

	void deleteByTokenHash(String tokenHash);
}
