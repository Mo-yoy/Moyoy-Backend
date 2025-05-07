package com.moyo.backend.security.jwt.repository;

import com.moyo.backend.security.jwt.domain.JwtWhiteList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JwtWhiteListRepositoryImpl implements JwtWhiteListRepository {

    private final JwtWhiteListJpaRepositoryImpl jwtWhitelistJpaRepositoryImpl;

    @Override
    public void save(JwtWhiteList jwtWhitelist) {
        jwtWhitelistJpaRepositoryImpl.save(jwtWhitelist);
    }

    @Override
    public boolean existByTokenValue(String jwt) {

        return jwtWhitelistJpaRepositoryImpl.existsByValue(jwt);
    }

    @Override
    public void deleteByTokenValue(String jwt) {

        jwtWhitelistJpaRepositoryImpl.deleteByValue(jwt);
    }
}
