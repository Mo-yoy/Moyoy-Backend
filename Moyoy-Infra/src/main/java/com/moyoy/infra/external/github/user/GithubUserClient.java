package com.moyoy.infra.external.github.user;

import feign.Response;

public interface GithubUserClient {
	GithubUserResponse fetchUser(Long userId, Integer githubUserId);

	GithubUserResponse fetchUser(String accessToken, Integer githubUserId);

	Response fetchUserRawResponse(String accessToken, Integer githubUserId);

	Response fetchUserRawResponse(Long userId, Integer githubUserId);
}
