package com.moyo.backend.security.oauth.repository;

import java.util.Date;

public interface LoginRepository {
    void save(Long userId, String refreshToken, Date expiration);

    String findWhiteListTokenKey(Long userId, String jwtRefreshToken);

    void delete(String key);
}