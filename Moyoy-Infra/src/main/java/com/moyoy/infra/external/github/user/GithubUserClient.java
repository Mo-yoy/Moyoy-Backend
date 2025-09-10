package com.moyoy.infra.external.github.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.infra.database.mysql.query.port.GithubTokenReader;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

import feign.Response;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubUserClient {

	private final GithubUserApi githubUserApi;
	private final GithubTokenReader githubTokenReader;

	public GithubUserResponse fetchUser(Long userId, Integer githubUserId) {

		String accessToken = githubTokenReader.findAccessTokenWithTokenType(userId).orElseThrow(() -> new IllegalStateException("User Github Token Not Found"));

		return githubUserApi.fetchUser(accessToken, githubUserId);
	}

	public GithubUserResponse fetchUser(String accessToken, Integer githubUserId) {

		return githubUserApi.fetchUser(accessToken, githubUserId);
	}

	public Response fetchUserRawResponse(String accessToken, Integer githubUserId) {
		return githubUserApi.fetchUserRawResponse(accessToken, githubUserId);
	}

	public Response fetchUserRawResponse(Long userId, Integer githubUserId) {

		String accessToken = githubTokenReader.findAccessTokenWithTokenType(userId).orElseThrow();

		return githubUserApi.fetchUserRawResponse(accessToken, githubUserId);
	}
}
