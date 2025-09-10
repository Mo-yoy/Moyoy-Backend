package com.moyoy.api.github_follow.application;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.moyoy.domain.github_follow.GithubUser;
import com.moyoy.domain.user.User;
import com.moyoy.domain.user.UserRepository;
import com.moyoy.domain.user.error.UserNotFoundException;

import com.moyoy.infra.database.redis.follow.GithubFollowSnapshotCacheManager;
import com.moyoy.infra.external.github.follow.GithubFollowClient;
import com.moyoy.infra.external.github.user.GithubUserClient;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubFollowCommandService {

	private final GithubFollowClient githubFollowClient;
	private final GithubUserClient githubUserClient;
	private final GithubFollowSnapshotCacheManager followSnapshotCacheManager;
	private final UserRepository userRepository;

	public void follow(Long currentUserId, Integer targetUserGithubId) {

		User currentMoyoyUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
		githubFollowClient.follow(currentUserId, targetUserGithubId);

		followSnapshotCacheManager.findFollowSnapshot(currentUserId)
			.ifPresent(currentUserSnapshot -> {

				GithubUser targetGithubUser = fromApi(currentUserId, targetUserGithubId);
				currentUserSnapshot.addFollowing(targetGithubUser);
				followSnapshotCacheManager.save(currentMoyoyUser.getId(), currentUserSnapshot);
			});

		Optional<User> targetMoyoyUser = userRepository.findByGithubUserId(targetUserGithubId);
		if (targetMoyoyUser.isEmpty())
			return;

		followSnapshotCacheManager.findFollowSnapshot(targetMoyoyUser.get().getId())
			.ifPresent(
				targetUserSnapshot -> {

					GithubUser currentGithubUser = fromUser(currentMoyoyUser);
					targetUserSnapshot.addFollower(currentGithubUser);
					followSnapshotCacheManager.save(targetMoyoyUser.get().getId(), targetUserSnapshot);
				});
	}

	public void unfollow(Long currentUserId, Integer targetUserGithubId) {

		User currentMoyoyUser = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);
		githubFollowClient.unFollow(currentUserId, targetUserGithubId);

		followSnapshotCacheManager.findFollowSnapshot(currentUserId)
			.ifPresent(currentUserSnapshot -> {

				GithubUser targetGithubUser = fromApi(currentUserId, targetUserGithubId);
				currentUserSnapshot.removeFollowing(targetGithubUser);
				followSnapshotCacheManager.save(currentMoyoyUser.getId(), currentUserSnapshot);
			});

		Optional<User> targetMoyoyUser = userRepository.findByGithubUserId(targetUserGithubId);
		if (targetMoyoyUser.isEmpty())
			return;

		followSnapshotCacheManager.findFollowSnapshot(targetMoyoyUser.get().getId())
			.ifPresent(
				targetUserSnapshot -> {

					GithubUser currentGithubUser = fromUser(currentMoyoyUser);
					targetUserSnapshot.removeFollower(currentGithubUser);
					followSnapshotCacheManager.save(targetMoyoyUser.get().getId(), targetUserSnapshot);
				});
	}

	private GithubUser fromApi(Long currentUserId, Integer targetGithubUserId) {

		GithubUserResponse res = githubUserClient.fetchUser(currentUserId, targetGithubUserId);
		return GithubUser.of(res.id(), res.login(), res.avatarUrl());
	}

	private GithubUser fromUser(User user) {
		return GithubUser.of(user.getGithubUserId(), user.getUsername(), user.getProfileImgUrl());
	}
}
