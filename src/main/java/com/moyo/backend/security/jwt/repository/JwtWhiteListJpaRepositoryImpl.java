package com.moyo.backend.security.jwt.repository;

import com.moyo.backend.security.jwt.domain.JwtWhiteList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtWhiteListJpaRepositoryImpl extends JpaRepository<JwtWhiteList, Long> {

    boolean existsByValue(String jwt);
    void deleteByValue(String jwt);
}
