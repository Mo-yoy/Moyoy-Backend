package com.moyoy.infra.external.github.user;

import feign.Response;

public interface GithubUserClient {
	GithubUserResponse fetchUser(String accessToken, Integer githubUserId);

	Response fetchUserRawResponse(String accessToken, Integer githubUserId);
}
