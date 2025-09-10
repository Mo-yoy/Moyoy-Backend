package com.moyoy.infra.external.github.support;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.infra.external.github.support.error.GithubApiLimitExceedException;
import com.moyoy.infra.external.github.user.GithubUserClient;

import feign.Response;

/**
 *  Github User Info API를 이용해서 API LIMIT 체크
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubApiLimitChecker {

	private final GithubUserClient githubUserClient;

	public void assertCanGithubRequest(String accessToken, Integer githubUserId) {

		Response response = githubUserClient.fetchUserRawResponse(accessToken, githubUserId);

		String remainingHeader = response.headers()
			.getOrDefault(GITHUB_RATE_LIMIT_HEADER, List.of())
			.stream()
			.findFirst()
			.orElse("0");

		int apiLimitRemaining = remainingHeader.isBlank() ? 0 : Integer.parseInt(remainingHeader);

		log.info("API Limit Remaining : {} | GitHub User Id : {}", apiLimitRemaining, githubUserId);

		if (apiLimitRemaining < GITHUB_MIN_REQUEST_THRESHOLD) {
			log.warn("API Limit Exceeded | GitHub User Id : {}, Remaining : {}", githubUserId, apiLimitRemaining);
			throw new GithubApiLimitExceedException();
		}
	}

	public void assertCanGithubRequest(Long userId, Integer githubUserId) {

		Response response = githubUserClient.fetchUserRawResponse(userId, githubUserId);

		String remainingHeader = response.headers()
			.getOrDefault(GITHUB_RATE_LIMIT_HEADER, List.of())
			.stream()
			.findFirst()
			.orElse("0");

		int apiLimitRemaining = remainingHeader.isBlank() ? 0 : Integer.parseInt(remainingHeader);

		log.info("API Limit Remaining : {} | GitHub User Id : {}", apiLimitRemaining, githubUserId);

		if (apiLimitRemaining < GITHUB_MIN_REQUEST_THRESHOLD) {
			log.warn("API Limit Exceeded | GitHub User Id : {}, Remaining : {}", githubUserId, apiLimitRemaining);
			throw new GithubApiLimitExceedException();
		}
	}
}
