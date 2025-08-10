package com.moyoy.domain.auth.data_access;

import com.moyoy.domain.auth.implement.JwtRefreshToken;

public interface JwtRefreshTokenRepository {

	void save(JwtRefreshToken jwtRefreshToken);

	boolean existByTokenValue(String tokenValue);

	void deleteByTokenValue(String tokenValue);

	void flush();
}
