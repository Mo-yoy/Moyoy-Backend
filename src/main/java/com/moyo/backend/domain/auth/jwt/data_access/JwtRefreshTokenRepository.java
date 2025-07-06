package com.moyo.backend.domain.auth.jwt.data_access;

import com.moyo.backend.domain.auth.jwt.implement.JwtRefreshToken;

// 성능 테스트 결과나 서버 예산 증가 가능 여부에 따라서 저장소가 변경 될 수 있음.
public interface JwtRefreshTokenRepository {

	void save(JwtRefreshToken jwtRefreshToken);

	boolean existByTokenValue(String tokenValue);

	void deleteByTokenValue(String tokenValue);

	void flush();
}
