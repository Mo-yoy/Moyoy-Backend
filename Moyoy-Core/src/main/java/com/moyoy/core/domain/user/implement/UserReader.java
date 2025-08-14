package com.moyoy.core.domain.user.implement;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.moyoy.common.exception.user.UserNotFoundException;
import com.moyoy.infra.database.domain.user.UserCountAndLastId;
import com.moyoy.infra.database.domain.user.UserEntity;
import com.moyoy.infra.database.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserReader {

	private final UserRepository userRepository;

	public Optional<User> findById(Long userId) {

		UserEntity userEntity = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		return Optional.ofNullable(UserMapper.toModel(userEntity));
	}

	public Optional<User> findByGithubUserId(Integer githubUserId) {

		UserEntity userEntity = userRepository.findByGithubUserId(githubUserId).orElseThrow(UserNotFoundException::new);
		return Optional.ofNullable(UserMapper.toModel(userEntity));
	}

	public List<User> findAllById(List<Long> ids) {
		return userRepository.findByIdIn(ids).stream()
			.map(UserMapper::toModel)
			.toList();
	}

	public List<User> findAll(Long lastUserId, int size) {
		return userRepository.findAll(lastUserId, size).stream()
			.map(UserMapper::toModel)
			.toList();
	}

	public UserStats getUserStats() {

		UserCountAndLastId userCountAndLastId = userRepository.fetchUserCountAndLastId();
		return new UserStats(userCountAndLastId.userCount(), userCountAndLastId.lastUserId());
	}
}
