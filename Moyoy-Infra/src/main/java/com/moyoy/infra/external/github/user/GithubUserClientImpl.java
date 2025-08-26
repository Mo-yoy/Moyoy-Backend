package com.moyoy.infra.external.github.user;

import org.springframework.stereotype.Component;

import com.moyoy.infra.external.github.helper.GithubOAuthTokenReader;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubUserClientImpl implements GithubUserClient{

	private final GithubUserFeignClient githubUserFeignClient;
	private final GithubOAuthTokenReader githubOAuthTokenReader;

	@Override
	public GithubUserResponse fetchUser(Long userId, Integer githubUserId) {

		String accessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

		return githubUserFeignClient.fetchUser(accessToken, githubUserId);
	}

	@Override
	public Response fetchUserRawResponse(Long userId, Integer githubUserId) {

		String accessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

		return githubUserFeignClient.fetchUserRawResponse(accessToken, githubUserId);
	}
}
