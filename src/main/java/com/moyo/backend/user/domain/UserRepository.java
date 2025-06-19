package com.moyo.backend.user.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface UserRepository{

    Optional<User> findById(Long userId);

    void save(User user);

    boolean existsById(Long userId);

    Slice<User> findAll(Pageable pageable);
}
