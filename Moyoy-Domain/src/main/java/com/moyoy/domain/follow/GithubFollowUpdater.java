package com.moyoy.domain.follow;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.moyoy.common.exception.user.UserNotFoundException;
import com.moyoy.domain.common.github.GithubOAuthTokenReader;
import com.moyoy.domain.common.github.GithubUserProfileDto;
import com.moyoy.infra.github.feign.GithubFollowClient;
import com.moyoy.domain.user.implement.User;
import com.moyoy.domain.user.implement.UserReader;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GithubFollowUpdater {

	private final GithubUserReader githubUserReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final GithubFollowClient githubFollowClient;
	private final GithubFollowCacheManager githubFollowCacheManager;
	private final UserReader userReader;

	public void follow(Long currentUserId, Integer targetGithubUserId) {

		Integer githubUserId = userReader.findById(currentUserId).orElseThrow(UserNotFoundException::new).getGithubUserId();
		String accessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

		GithubFollowUser currentUser = githubUserReader.fetchGithubUser(githubUserId, accessToken);
		GithubFollowUser targetUser = githubUserReader.fetchGithubUser(targetGithubUserId, accessToken);

		ResponseEntity<Void> followResult = githubFollowClient.follow(targetUser.username(), accessToken);

		int responseStatus = followResult.getStatusCode().value();

		if (responseStatus == 204) {

			githubFollowCacheManager.addFollowingToCurrentUser(currentUserId, targetUser);

			Optional<User> followedMoyoyUser = userReader.findByGithubUserId(targetGithubUserId);

			if (followedMoyoyUser.isPresent()) {

				Long targetUserId = followedMoyoyUser.get().getId();
				githubFollowCacheManager.addFollowerToTargetUser(targetUserId, currentUser);
			}
		}
	}

	public void unfollow(Long currentUserId, Integer targetGithubUserId) {

		Integer githubUserId = userReader.findById(currentUserId).orElseThrow(UserNotFoundException::new).getGithubUserId();
		String accessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

		GithubFollowUser currentUser = githubUserReader.fetchGithubUser(githubUserId, accessToken);
		GithubFollowUser targetUser = githubUserReader.fetchGithubUser(targetGithubUserId, accessToken);

		ResponseEntity<Void> unfollowResult = githubFollowClient.unfollow(targetUser.username(), accessToken);

		int responseStatus = unfollowResult.getStatusCode().value();

		if (responseStatus == 204) {

			githubFollowCacheManager.deleteFollowingToCurrentUser(currentUserId, targetUser);

			Optional<User> unfollowedMoyoyUser = userReader.findByGithubUserId(targetGithubUserId);

			if (unfollowedMoyoyUser.isPresent()) {

				Long targetUserId = unfollowedMoyoyUser.get().getId();
				githubFollowCacheManager.deleteFollowerToTargetUser(targetUserId, currentUser);
			}
		}
	}

}
