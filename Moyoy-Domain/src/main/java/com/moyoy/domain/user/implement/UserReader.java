package com.moyoy.domain.user.implement;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.moyoy.domain.ranking.data_access.UserCountAndLastId;
import com.moyoy.domain.user.data_access.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserReader {

	private final UserRepository userRepository;

	public Optional<User> findById(Long userId) {
		return userRepository.findById(userId);
	}

	public Optional<User> findByGithubUserId(Integer githubUserId) {
		return userRepository.findByGithubUserId(githubUserId);
	}

	public List<User> findAllById(List<Long> userIds) {
		return userRepository.findByIdIn(userIds);
	}

	public List<User> findAll(Long lastUserId, int size) {
		return userRepository.findAll(lastUserId, size);
	}

	public UserStats getUserStats() {

		UserCountAndLastId userCountAndLastId = userRepository.fetchUserCountAndLastId();
		return new UserStats(userCountAndLastId.userCount(), userCountAndLastId.lastUserId());
	}

}
