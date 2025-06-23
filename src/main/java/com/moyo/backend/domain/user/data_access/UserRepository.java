package com.moyo.backend.domain.user.data_access;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.moyo.backend.domain.user.implement.User;

public interface UserRepository {

	Optional<User> findById(Long userId);

	void save(User user);

	boolean existsById(Long userId);

	Slice<User> findAll(Pageable pageable);
}
