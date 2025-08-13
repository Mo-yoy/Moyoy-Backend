package com.moyoy.core.domain.follow.implement;

import org.springframework.stereotype.Component;

import com.moyoy.core.domain.user.implement.GithubUserProfileMeta;
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

	public GithubUserProfileMeta fetchGithubUserProfile(Integer githubUserId, String accessToken) {

		GithubProfileResponse githubProfileResponse = githubProfileClient.fetchUserProfile(accessToken, githubUserId);

		return GithubUserProfileMeta.from(githubProfileResponse);
	}

}
