package com.moyo.backend.githubFollow.application;

import org.springframework.stereotype.Service;

import com.moyo.backend.githubFollow.domain.GithubFollowUser;
import com.moyo.backend.githubFollow.infrastructure.GithubFollowCacheManager;
import com.moyo.backend.githubFollow.infrastructure.GithubFollowHttpClient;
import com.moyo.backend.security.oauth.GithubOAuthTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubFollowCommandService {

	private final GithubFollowCacheManager githubFollowCacheManager;
	private final GithubOAuthTokenProvider githubOAuthTokenProvider;
	private final GithubFollowHttpClient githubFollowHttpClient;

	public void clearFollowCache(Long currentUserId) {
		githubFollowCacheManager.evictCache(currentUserId);
	}

	public void follow(Long currentUserId, Long targetUserId) {

		String oauthAccessToken = githubOAuthTokenProvider.getGithubAccessToken(currentUserId);
		GithubFollowUser currentUser = githubFollowHttpClient.fetchGithubFollowUserById(currentUserId, oauthAccessToken);
		GithubFollowUser targetUser = githubFollowHttpClient.fetchGithubFollowUserById(targetUserId, oauthAccessToken);

		int responseStatus = githubFollowHttpClient.follow(targetUser.username(), oauthAccessToken);

		if (responseStatus == 204) {

			githubFollowCacheManager.addFollowingToCurrentUser(currentUserId, targetUser);
			githubFollowCacheManager.addFollowerToTargetUser(targetUserId, currentUser);
		}
	}

	public void unfollow(Long currentUserId, Long targetUserId) {

		String oauthAccessToken = githubOAuthTokenProvider.getGithubAccessToken(currentUserId);
		GithubFollowUser currentUser = githubFollowHttpClient.fetchGithubFollowUserById(currentUserId, oauthAccessToken);
		GithubFollowUser targetUser = githubFollowHttpClient.fetchGithubFollowUserById(targetUserId, oauthAccessToken);

		int responseStatus = githubFollowHttpClient.unfollow(targetUser.username(), oauthAccessToken);

		if (responseStatus == 204) {

			githubFollowCacheManager.deleteFollowingToCurrentUser(currentUserId, targetUser);
			githubFollowCacheManager.deleteFollowerToTargetUser(targetUserId, currentUser);
		}
	}
}
