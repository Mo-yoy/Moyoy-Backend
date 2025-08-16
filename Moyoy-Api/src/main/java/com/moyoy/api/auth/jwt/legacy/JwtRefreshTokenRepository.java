package com.moyoy.api.auth.jwt.legacy;

public interface JwtRefreshTokenRepository {

	void save(JwtRefreshToken jwtRefreshToken);

	boolean existByTokenValue(String tokenValue);

	void deleteByTokenValue(String tokenValue);

	void flush();
}
