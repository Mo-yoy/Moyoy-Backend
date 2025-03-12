package com.moyo.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByProviderId(String providerId);
    boolean existsByProviderId(String providerId);
}
