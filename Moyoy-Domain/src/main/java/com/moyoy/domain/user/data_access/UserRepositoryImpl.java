package com.moyoy.domain.user.data_access;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moyoy.domain.ranking.data_access.UserCountAndLastId;
import com.moyoy.domain.user.implement.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;
	private final UserQueryDslRepository userQueryDslRepository;

	@Override
	public Optional<User> findById(Long userId) {
		return userJpaRepository.findById(userId);
	}

	@Override
	public Optional<User> findByGithubUserId(Integer githubUserId) {
		return userJpaRepository.findByGithubUserId(githubUserId);
	}

	@Override
	public void save(User user) {
		userJpaRepository.save(user);
	}

	@Override
	public List<User> findByIdIn(List<Long> userIds) {
		return userJpaRepository.findAllById(userIds);
	}

	@Override
	public List<User> findAll(Long lastUserId, int size) {
		return userQueryDslRepository.findAll(lastUserId, size);
	}

	@Override
	public UserCountAndLastId fetchUserCountAndLastId() {
		return userQueryDslRepository.fetchUserCountAndLastId();
	}

}
