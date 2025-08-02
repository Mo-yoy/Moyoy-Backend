package com.moyo.backend.domain.user.implement;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.batchLegacy.ranking.data_access.UserCountAndLastId;
import com.moyo.backend.domain.batchLegacy.ranking.implement.UserRankingBatchSnapshot;
import com.moyo.backend.domain.user.data_access.UserRepository;

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

	public UserRankingBatchSnapshot getUserBatchSnapshot() {
		UserCountAndLastId userCountAndLastId = userRepository.fetchUserCountAndLastId();
		return UserRankingBatchSnapshot.from(userCountAndLastId);
	}
}
