package com.moyoy.core.domain.github;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.moyoy.core.support.error.github.GithubApiLimitExceedException;
import com.moyoy.infra.client.github.feign.GithubProfileClient;
import com.moyoy.infra.client.github.dto.GithubProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
