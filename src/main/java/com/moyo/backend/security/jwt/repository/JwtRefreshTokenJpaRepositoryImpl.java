package com.moyo.backend.security.jwt.repository;

import com.moyo.backend.security.jwt.domain.JwtRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtRefreshTokenJpaRepositoryImpl extends JpaRepository<JwtRefreshToken, Long> {

    boolean existsByValue(String tokenValue);
    void deleteByValue(String tokenValue);
}
