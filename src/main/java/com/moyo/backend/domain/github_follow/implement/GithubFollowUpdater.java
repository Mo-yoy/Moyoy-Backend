package com.moyo.backend.domain.github_follow.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.auth.oauth.GithubOAuthTokenReader;
import com.moyo.backend.domain.github_follow.data_access.GithubFollowHttpClient;

@Component
@RequiredArgsConstructor
public class GithubFollowUpdater {

	private final GithubUserReader githubUserReader;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final GithubFollowHttpClient githubFollowHttpClient;
	private final GithubFollowCacheManager githubFollowCacheManager;

	public void follow(Long currentUserId, Long targetUserId) {

		String oauthAccessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);
		GithubUser currentUser = githubUserReader.getGithubUser(currentUserId, oauthAccessToken);
		GithubUser targetUser = githubUserReader.getGithubUser(targetUserId, oauthAccessToken);

		int responseStatus = githubFollowHttpClient.follow(targetUser.username(), oauthAccessToken);

		if (responseStatus == 204) {

			githubFollowCacheManager.addFollowingToCurrentUser(currentUserId, targetUser);
			githubFollowCacheManager.addFollowerToTargetUser(targetUserId, currentUser);
		}
	}

	public void unfollow(Long currentUserId, Long targetUserId) {

		String oauthAccessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);
		GithubUser currentUser = githubUserReader.getGithubUser(currentUserId, oauthAccessToken);
		GithubUser targetUser = githubUserReader.getGithubUser(targetUserId, oauthAccessToken);

		int responseStatus = githubFollowHttpClient.unfollow(targetUser.username(), oauthAccessToken);

		if (responseStatus == 204) {

			githubFollowCacheManager.deleteFollowingToCurrentUser(currentUserId, targetUser);
			githubFollowCacheManager.deleteFollowerToTargetUser(targetUserId, currentUser);
		}
	}

}
