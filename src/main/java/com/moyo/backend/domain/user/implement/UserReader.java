package com.moyo.backend.domain.user.implement;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

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

	public List<Long> findAllUserIdList() {

		return userRepository.findAllUserIdList();
	}

	public List<User> findAllById(List<Long> userIds) {
		return userRepository.findByIdIn(userIds);
	}
}
