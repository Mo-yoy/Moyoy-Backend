package com.moyoy.infra.database.domain.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;


import com.moyoy.infra.database.domain.ranking.UserCountAndLastId;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;
	private final UserQueryDslRepository userQueryDslRepository;

	@Override
	public Optional<UserEntity> findById(Long userId) {
		return userJpaRepository.findById(userId);
	}

	@Override
	public Optional<UserEntity> findByGithubUserId(Integer githubUserId) {
		return userJpaRepository.findByGithubUserId(githubUserId);
	}

	@Override
	public void save(UserEntity user) {
		userJpaRepository.save(user);
	}

	@Override
	public List<UserEntity> findByIdIn(List<Long> userIds) {
		return userJpaRepository.findAllById(userIds);
	}

	@Override
	public List<UserEntity> findAll(Long lastUserId, int size) {
		return userQueryDslRepository.findAll(lastUserId, size);
	}

	@Override
	public UserCountAndLastId fetchUserCountAndLastId() {
		return userQueryDslRepository.fetchUserCountAndLastId();
	}

}
