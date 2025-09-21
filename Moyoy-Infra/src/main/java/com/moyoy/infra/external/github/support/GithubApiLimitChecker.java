package com.moyoy.infra.external.github.support;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.infra.external.github.support.error.GithubPreCheckLimitExceedException;
import com.moyoy.infra.external.github.user.GithubUserClient;

import feign.Response;

/**
 *  Github User Info API를 이용해서 API LIMIT 체크
 *
 *  TODO : 좀 이상함.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubApiLimitChecker {

	private final GithubUserClient githubUserClient;

	public void assertCanGithubRequest(String accessToken, Long userId, Integer githubUserId) {

		String remainingHeader;
		try (Response response = githubUserClient.fetchUserRawResponse(accessToken, githubUserId)) {

			remainingHeader = response.headers()
				.getOrDefault(GITHUB_RATE_LIMIT_HEADER, List.of())
				.stream()
				.findFirst()
				.orElse("0");
		}

		int apiLimitRemaining = remainingHeader.isBlank() ? 0 : Integer.parseInt(remainingHeader);

		log.info("User Id : {} | API Limit Remaining : {}", userId, apiLimitRemaining);

		if (apiLimitRemaining < GITHUB_MIN_REQUEST_THRESHOLD) {

			log.error("API Limit Exceeded | User Id : {}, Remaining : {}", userId, apiLimitRemaining);
			throw new GithubPreCheckLimitExceedException();
		}
	}
}
