package com.moyoy.api.github_follow.application;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.moyoy.api.github_follow.application.helper.GithubFollowCacheUpdaterL;

import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.domain.user.error.UserGithubTokenNotFoundException;
import com.moyoy.domain.user.error.UserNotFoundException;

import com.moyoy.infra.database.mysql.query.port.GithubTokenReader;
import com.moyoy.infra.external.github.follow.GithubFollowClient;
import com.moyoy.infra.external.github.user.GithubUserClient;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;
import com.moyoy.infra.redis.cache.github_follow.GithubUserProfile;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubFollowCommandService {

	private final GithubFollowCacheUpdaterL followCacheUpdater;
	private final GithubTokenReader githubTokenReader;
	private final GithubFollowClient githubFollowClient;
	private final UserRepository userRepository;
	private final GithubUserClient githubUserClient;


	public void follow(Long currentUserId, Integer targetUserGithubId) {

		String bearerToken = githubTokenReader.findAccessBearerToken(currentUserId)
			.orElseThrow(UserGithubTokenNotFoundException::new);

		githubFollowClient.follow(bearerToken, currentUserId, targetUserGithubId);

		followCacheUpdater.addFollowingToCache(
			currentUserId,
			() -> {
				GithubUserResponse targetGithubUserResponse = githubUserClient.fetchUser(bearerToken, targetUserGithubId);
				return GithubUserProfile.from(targetGithubUserResponse);
			});

		Optional<User> targetUserOpt = userRepository.findByGithubUserId(targetUserGithubId);
		if (targetUserOpt.isEmpty()) return;

		followCacheUpdater.addFollowerToCache(
			targetUserOpt.get().getId(),
			() -> {
				User currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
				return GithubUserProfile.of(currentUser.getGithubUserId(), currentUser.getUsername(), currentUser.getProfileImgUrl());
			});
	}

	public void unfollow(Long currentUserId, Integer targetUserGithubId) {

		String bearerToken = githubTokenReader.findAccessBearerToken(currentUserId)
			.orElseThrow(UserGithubTokenNotFoundException::new);

		githubFollowClient.unfollow(bearerToken, currentUserId, targetUserGithubId);

		followCacheUpdater.removeFollowingFromCache(
			currentUserId,
			() -> {
				GithubUserResponse targetGithubUserResponse = githubUserClient.fetchUser(bearerToken, targetUserGithubId);
				return GithubUserProfile.from(targetGithubUserResponse);
			});

		Optional<User> targetUserOpt = userRepository.findByGithubUserId(targetUserGithubId);
		if (targetUserOpt.isEmpty())
			return;

		followCacheUpdater.removeFollowerFromCache(
			targetUserOpt.get().getId(),
			() -> {
				User currentUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
				return GithubUserProfile.of(currentUser.getGithubUserId(), currentUser.getUsername(), currentUser.getProfileImgUrl());
			});
	}
}
