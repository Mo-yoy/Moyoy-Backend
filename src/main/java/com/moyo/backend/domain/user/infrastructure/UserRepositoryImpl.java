package com.moyo.backend.domain.user.infrastructure;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.moyo.backend.domain.user.domain.User;
import com.moyo.backend.domain.user.domain.UserRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public Optional<User> findById(Long userId) {
		return userJpaRepository.findById(userId);
	}

	@Override
	public void save(User user) {
		userJpaRepository.save(user);
	}

	@Override
	public boolean existsById(Long userId) {
		return userJpaRepository.existsById(userId);
	}

	@Override
	public Slice<User> findAll(Pageable pageable) {

		return userJpaRepository.findAll(pageable);
	}
}
