package com.moyoy.infra.external.github.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.infra.external.github.helper.GithubOAuthTokenReader;

import feign.Response;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubUserClientImpl implements GithubUserClient {

	private final GithubUserFeignClient githubUserFeignClient;
	private final GithubOAuthTokenReader githubOAuthTokenReader;

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "userFetchFallBack")
	public GithubUserResponse fetchUser(Long userId, Integer githubUserId) {

		String accessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

		return githubUserFeignClient.fetchUser(accessToken, githubUserId);
	}

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "userFetchFallBack")
	public GithubUserResponse fetchUser(String accessToken, Integer githubUserId) {

		return githubUserFeignClient.fetchUser(accessToken, githubUserId);
	}

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "userFetchFallBack")
	public Response fetchUserRawResponse(String accessToken, Integer githubUserId) {
		return githubUserFeignClient.fetchUserRawResponse(accessToken, githubUserId);
	}

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "userFetchFallBack")
	public Response fetchUserRawResponse(Long userId, Integer githubUserId) {

		String accessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

		return githubUserFeignClient.fetchUserRawResponse(accessToken, githubUserId);
	}

	private void userFetchFallBack(Long userId, Integer githubUserId, Throwable throwable) {
		if (throwable instanceof CallNotPermittedException) {

		}
		/// TODO : 에러코드 회의 후 처리
	}

}
