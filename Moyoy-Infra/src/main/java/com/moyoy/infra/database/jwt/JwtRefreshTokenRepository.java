package com.moyoy.infra.database.jwt;

public interface JwtRefreshTokenRepository {

	void save(JwtRefreshToken jwtRefreshToken);

	boolean existByTokenValue(String tokenValue);

	void deleteByTokenValue(String tokenValue);

	void flush();
}
