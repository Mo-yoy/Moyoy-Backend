package com.moyo.backend.security.oauth.repository;

import java.util.Date;

public interface LoginRepository {
    void save(String providerId, String refreshToken, Date expiration);

    String findWhiteListTokenKey(String providerId, String jwtRefreshToken);

    void delete(String key);
}