package com.moyo.backend.domain.github_follow.implement;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.moyo.backend.domain.github_follow.data_access.GithubFollowHttpClient;

@Component
@RequiredArgsConstructor
public class GithubUserReader {

	private final GithubFollowHttpClient githubFollowHttpClient;

	public GithubUser getGithubUser(Long userId, String oauthAccessToken) {

		return githubFollowHttpClient.fetchGithubFollowUserById(userId, oauthAccessToken);
	}
}
