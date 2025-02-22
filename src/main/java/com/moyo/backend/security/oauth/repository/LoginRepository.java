package com.moyo.backend.security.oauth.repository;

import java.util.Date;

public interface LoginRepository {
    void save(String memberAppId, String refreshToken, Date expiration);

    String findWhiteListTokenKey(String memberAppId, String jwtRefreshToken);

    void delete(String key);
}