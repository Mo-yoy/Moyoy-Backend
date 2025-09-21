package com.moyoy.infra.external.github.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

import feign.Response;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubUserClient {

	private final GithubUserApi githubUserApi;

	public GithubUserResponse fetchUser(String bearerToken, Integer githubUserId) {

		return githubUserApi.fetchUser(bearerToken, githubUserId);
	}

	public Response fetchUserRawResponse(String bearerToken, Integer githubUserId) {
		return githubUserApi.fetchUserRawResponse(bearerToken, githubUserId);
	}
}
