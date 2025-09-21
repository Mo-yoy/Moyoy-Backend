package com.moyoy.infra.external.github.follow;

import static com.moyoy.common.constant.MoyoConstants.*;
import static com.moyoy.infra.external.github.support.GithubPagedApiUtils.*;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.infra.external.github.support.GithubResponseParser;
import com.moyoy.infra.external.github.support.error.GithubPreCheckLimitExceedException;
import com.moyoy.infra.external.github.user.GithubUserApi;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;
import com.moyoy.infra.redis.cache.github_follow.GithubUserProfile;

import feign.Response;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowClient {

	private static final String FOLLOWING = "팔로잉";
	private static final String FOLLOWER = "팔로워";
	private static final String FOLLOW = "팔로우";
	private static final String UNFOLLOW = "언팔로우";

	private final GithubFollowApi githubFollowApi;
	private final GithubUserApi githubUserApi;
	private final GithubResponseParser responseParser;

	public List<GithubUserProfile> fetchFollowings(String bearerToken, Long userId, Integer githubUserId) {

		GithubUserResponse githubUserResponse;
		int rateLimitRemaining;

		try (Response userRawResponse = githubUserApi.fetchUserRawResponse(bearerToken, githubUserId)) {

			githubUserResponse = responseParser.parseBody(userRawResponse, GithubUserResponse.class);
			rateLimitRemaining = responseParser.extractRateLimit(userRawResponse);
		}

		int followingPageSize = calculateMaxPage(githubUserResponse.following());
		logFollowQuery(FOLLOWING, userId, followingPageSize);

		boolean canRequest = isRateLimitExceeded(rateLimitRemaining, followingPageSize);
		if (!canRequest) {

			logFollowQueryError(FOLLOWING, userId, rateLimitRemaining);
			throw new GithubPreCheckLimitExceedException();
		}

		List<GithubUserProfile> githubFollowings = new ArrayList<>();

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= followingPageSize; currentPage++) {

			List<GithubUserResponse> followingsResponseList = githubFollowApi.fetchPagedFollowings(bearerToken, GITHUB_MAX_QUERY_PAGING_SIZE, currentPage);

			githubFollowings.addAll(
				followingsResponseList.stream()
					.filter(userResponse -> GITHUB_OAUTH2_USER_TYPE_USER.equalsIgnoreCase(userResponse.type()))
					.map(userResponse -> new GithubUserProfile(userResponse.id(), userResponse.login(), userResponse.avatarUrl()))
					.toList());
		}

		return githubFollowings;
	}

	public List<GithubUserProfile> fetchFollowers(String bearerToken, Long userId, Integer githubUserId) {

		GithubUserResponse githubUserResponse;
		int rateLimitRemaining;

		try (Response userRawResponse = githubUserApi.fetchUserRawResponse(bearerToken, githubUserId)) {

			githubUserResponse = responseParser.parseBody(userRawResponse, GithubUserResponse.class);
			rateLimitRemaining = responseParser.extractRateLimit(userRawResponse);
		}

		int followerPageSize = calculateMaxPage(githubUserResponse.followers());
		logFollowQuery(FOLLOWER, userId, followerPageSize);

		boolean canRequest = isRateLimitExceeded(rateLimitRemaining, followerPageSize);
		if (!canRequest) {

			logFollowQueryError(FOLLOWER, userId, rateLimitRemaining);
			throw new GithubPreCheckLimitExceedException();
		}

		List<GithubUserProfile> githubFollowers = new ArrayList<>();

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= followerPageSize; currentPage++) {

			List<GithubUserResponse> followersResponseList = githubFollowApi.fetchPagedFollowers(bearerToken, GITHUB_MAX_QUERY_PAGING_SIZE, currentPage);

			githubFollowers.addAll(
				followersResponseList.stream()
					.filter(userResponse -> GITHUB_OAUTH2_USER_TYPE_USER.equalsIgnoreCase(userResponse.type()))
					.map(userResponse -> new GithubUserProfile(userResponse.id(), userResponse.login(), userResponse.avatarUrl()))
					.toList());

		}

		return githubFollowers;
	}

	/**
	 *  Feign 호출 후 발생한 Error는 ErrorDecoder 우리 에러로 변경해서 넘김.
	 */
	public void follow(String bearerToken, Long userId, Integer targetUserGithubId) {

		GithubUserResponse targetUserResponse = githubUserApi.fetchUser(bearerToken, targetUserGithubId);
		githubFollowApi.follow(bearerToken, targetUserResponse.login());
		logFollowCommand(FOLLOW, userId, targetUserGithubId);
	}

	public void unfollow(String bearerToken, Long userId, Integer targetUserGithubId) {

		GithubUserResponse targetUserResponse = githubUserApi.fetchUser(bearerToken, targetUserGithubId);
		githubFollowApi.unfollow(bearerToken, targetUserResponse.login());
		logFollowCommand(UNFOLLOW, userId, targetUserGithubId);
	}

	private void logFollowQuery(String type, Long userId, int maxPageSize) {

		log.info("GitHub {} 리스트 조회 요청 | userId={}, pageSize={}, totalPages={}", type, userId, GITHUB_MAX_QUERY_PAGING_SIZE, maxPageSize);
	}

	private void logFollowQueryError(String type, Long userId, int rateLimitRemaining) {

		log.error("{} 리스트 조회가 API Limit 초과로 불가능 합니다. | userId : {}, 남은 API 호출 횟수 : {}", type, userId, rateLimitRemaining);
	}

	private void logFollowCommand(String type, Long userId, Integer targetUserGithubId) {

		log.info("깃허브 {} 요청 성공 | userId : {} -> targetUserGithubId : {}, ", type, userId, targetUserGithubId);
	}
}
