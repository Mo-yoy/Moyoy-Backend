package com.moyoy.api.auth.jwt.data_access;

import com.moyoy.api.auth.jwt.implement.JwtRefreshToken;

public interface JwtRefreshTokenRepository {

	void save(JwtRefreshToken jwtRefreshToken);

	boolean existByTokenValue(String tokenValue);

	void deleteByTokenValue(String tokenValue);

	void flush();
}
