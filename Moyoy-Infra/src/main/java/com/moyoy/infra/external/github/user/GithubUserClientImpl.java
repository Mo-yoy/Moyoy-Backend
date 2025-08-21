package com.moyoy.infra.external.github.user;

import org.springframework.stereotype.Component;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubUserClientImpl implements GithubUserClient{

	private final GithubUserFeignClient githubUserFeignClient;

	@Override
	public GithubUserResponse fetchUser(String accessToken, Integer githubUserId) {
		return githubUserFeignClient.fetchUser(accessToken, githubUserId);
	}

	@Override
	public Response fetchUserRawResponse(String accessToken, Integer githubUserId) {
		return githubUserFeignClient.fetchUserRawResponse(accessToken, githubUserId);
	}
}
