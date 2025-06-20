package com.moyo.backend.user.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserRepository {

	Optional<User> findById(Long userId);

	void save(User user);

	boolean existsById(Long userId);

	Slice<User> findAll(Pageable pageable);
}
