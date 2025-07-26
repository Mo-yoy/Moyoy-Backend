package com.moyo.backend.domain.github_follow.implement;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.common.exception.user.UserNotFoundException;
import com.moyo.backend.common.implement.GithubOAuthTokenReader;
import com.moyo.backend.domain.github_follow.data_access.GithubFollowHttpClient;
import com.moyo.backend.domain.user.implement.User;
import com.moyo.backend.domain.user.implement.UserReader;

@Component
@RequiredArgsConstructor
public class GithubFollowUpdater {

	private final GithubUserReader githubUserReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final GithubFollowHttpClient githubFollowHttpClient;
	private final GithubFollowCacheManager githubFollowCacheManager;
	private final UserReader userReader;

	public void follow(Long currentUserId, Integer targetGithubUserId) {

		Integer githubUserId = userReader.findById(currentUserId).orElseThrow(UserNotFoundException::new).getGithubUserId();
		String oauthAccessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

		GithubFollowUser currentUser = githubUserReader.getGithubUser(githubUserId, oauthAccessToken);
		GithubFollowUser targetUser = githubUserReader.getGithubUser(targetGithubUserId, oauthAccessToken);

		int responseStatus = githubFollowHttpClient.follow(targetUser.username(), oauthAccessToken);

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
		String oauthAccessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);

		GithubFollowUser currentUser = githubUserReader.getGithubUser(githubUserId, oauthAccessToken);
		GithubFollowUser targetUser = githubUserReader.getGithubUser(targetGithubUserId, oauthAccessToken);

		int responseStatus = githubFollowHttpClient.unfollow(targetUser.username(), oauthAccessToken);

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
