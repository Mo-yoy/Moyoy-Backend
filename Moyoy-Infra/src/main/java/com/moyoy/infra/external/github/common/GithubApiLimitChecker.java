package com.moyoy.infra.external.github.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.moyoy.domain.support.error.github.GithubApiLimitExceedException;
import com.moyoy.infra.external.github.dto.GithubProfileResponse;
import com.moyoy.infra.external.github.feign.GithubProfileClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubApiLimitChecker {

	private static final String RATE_LIMIT_HEADER = "X-RateLimit-Remaining";
	private static final int GITHUB_MIN_REQUEST_THRESHOLD = 2000;

	private final GithubProfileClient githubProfileClient;

	public void assertCanGithubRequest(String accessToken, Integer githubUserId) {

		ResponseEntity<GithubProfileResponse> response = githubProfileClient.fetchUserProfileEntity(accessToken, githubUserId);

		String remainingHeader = response.getHeaders().getFirst(RATE_LIMIT_HEADER);

		int apiLimitRemaining = (remainingHeader == null || remainingHeader.isBlank()) ? -1 : Integer.parseInt(remainingHeader);

		log.info("API Limit Remaining : {} | Github User Id : {}", apiLimitRemaining, githubUserId);

		if (apiLimitRemaining < GITHUB_MIN_REQUEST_THRESHOLD) {
			log.warn("API Limit Exceed | Github User Id : {} , Remaining : {}", githubUserId, apiLimitRemaining);
			throw new GithubApiLimitExceedException();
		}
	}
}
