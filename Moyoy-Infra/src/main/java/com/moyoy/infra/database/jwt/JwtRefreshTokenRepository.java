package com.moyoy.infra.database.jwt;

public interface JwtRefreshTokenRepository {

	void save(JwtRefreshToken jwtRefreshToken);

	boolean existByTokenHash(String tokenHash);

	void deleteByTokenValue(String tokenValue);

	void flush();
}
