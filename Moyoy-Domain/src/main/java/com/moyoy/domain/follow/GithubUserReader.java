package com.moyoy.domain.follow;

import org.springframework.stereotype.Component;

import com.moyoy.infra.github.feign.GithubProfileClient;
import com.moyoy.infra.github.dto.GithubProfileResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GithubUserReader {

	private final GithubProfileClient githubProfileClient;

	public GithubFollowUser fetchGithubUser(Integer githubUserId, String accessToken) {

		GithubProfileResponse githubProfileResponse = githubProfileClient.fetchUserProfile(accessToken, githubUserId);

		return GithubFollowUser.from(githubProfileResponse);
	}
}
