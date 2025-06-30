package com.moyo.backend.domain.user.data_access;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.moyo.backend.domain.user.implement.User;

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

	@Override
	public List<Long> findAllUserIdList() {
		return userJpaRepository.findAllUserIds();
	}

	@Override
	public List<User> findByIdIn(List<Long> userIds) {
		return userJpaRepository.findAllById(userIds);
	}
}
