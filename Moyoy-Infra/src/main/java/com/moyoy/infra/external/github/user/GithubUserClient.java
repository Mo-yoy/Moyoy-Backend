package com.moyoy.infra.external.github.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.infra.database.mysql.support.OAuthTokenReader;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

import feign.Response;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubUserClient {

	private final GithubUserApi githubUserApi;
	private final OAuthTokenReader OAuthTokenReader;

	public GithubUserResponse fetchUser(Long userId, Integer githubUserId) {

		String accessToken = OAuthTokenReader.getGithubAccessToken(userId);

		return githubUserApi.fetchUser(accessToken, githubUserId);
	}

	public GithubUserResponse fetchUser(String accessToken, Integer githubUserId) {

		return githubUserApi.fetchUser(accessToken, githubUserId);
	}

	public Response fetchUserRawResponse(String accessToken, Integer githubUserId) {
		return githubUserApi.fetchUserRawResponse(accessToken, githubUserId);
	}

	public Response fetchUserRawResponse(Long userId, Integer githubUserId) {

		String accessToken = OAuthTokenReader.getGithubAccessToken(userId);

		return githubUserApi.fetchUserRawResponse(accessToken, githubUserId);
	}
}
