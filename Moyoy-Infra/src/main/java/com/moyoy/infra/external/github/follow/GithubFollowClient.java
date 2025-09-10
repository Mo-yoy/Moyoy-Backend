package com.moyoy.infra.external.github.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.moyoy.domain.github_follow.GithubUser;

import com.moyoy.infra.database.mysql.support.OAuthTokenReader;
import com.moyoy.infra.external.github.support.GithubResponseParser;
import com.moyoy.infra.external.github.support.error.GithubApiLimitExceedException;
import com.moyoy.infra.external.github.user.GithubUserApi;
import com.moyoy.infra.external.github.user.dto.GithubUserResponse;

import feign.Response;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowClient {

	private final GithubFollowApi githubFollowApi;
	private final GithubUserApi githubUserApi;
	private final GithubResponseParser responseParser;
	private final OAuthTokenReader OAuthTokenReader;

	public List<GithubUser> fetchFollowings(Long userId, Integer githubUserId) {

		String accessToken = OAuthTokenReader.getGithubAccessToken(userId);
		Response userRawResponse = githubUserApi.fetchUserRawResponse(accessToken, githubUserId);
		GithubUserResponse githubUserResponse = responseParser.parseBody(userRawResponse, GithubUserResponse.class);
		int rateLimitRemaining = responseParser.extractRateLimit(userRawResponse);

		int maxFollowingPageSize = githubUserResponse.following() / GITHUB_MAX_QUERY_PAGING_SIZE + 1;
		log.info("{}의 깃허브 팔로잉 리스트 조회 요청 로그 (page size = {}) | followingMaxPage : {},", userId, GITHUB_MAX_QUERY_PAGING_SIZE, maxFollowingPageSize);

		if (rateLimitRemaining - maxFollowingPageSize < GITHUB_MIN_REQUEST_THRESHOLD) {
			log.error("팔로잉 리스트 조회 중 API Limit 초과 | GitHub User Id : {}, Remaining : {}", githubUserId, rateLimitRemaining);
			throw new GithubApiLimitExceedException();
		}

		List<GithubUser> githubFollowings = new ArrayList<>();

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= maxFollowingPageSize; currentPage++) {

			List<GithubUserResponse> followingsResponseList = githubFollowApi.fetchPagedFollowings(accessToken, GITHUB_MAX_QUERY_PAGING_SIZE, currentPage);

			githubFollowings.addAll(
				followingsResponseList.stream()
					.filter(userResponse -> "User".equalsIgnoreCase(userResponse.type()))
					.map(userResponse -> new GithubUser(userResponse.id(), userResponse.login(), userResponse.avatarUrl()))
					.toList());
		}

		return githubFollowings;
	}

	public List<GithubUser> fetchFollowers(Long userId, Integer githubUserId) {

		String accessToken = OAuthTokenReader.getGithubAccessToken(userId);
		Response userRawResponse = githubUserApi.fetchUserRawResponse(accessToken, githubUserId);
		GithubUserResponse githubUserResponse = responseParser.parseBody(userRawResponse, GithubUserResponse.class);
		int rateLimitRemaining = responseParser.extractRateLimit(userRawResponse);

		int maxFollowerPageSize = githubUserResponse.followers() / GITHUB_MAX_QUERY_PAGING_SIZE + 1;
		log.info("{}의 깃허브 팔로워 리스트 조회 요청 로그 (page size = {}) | followerMaxPage : {},", userId, GITHUB_MAX_QUERY_PAGING_SIZE, maxFollowerPageSize);

		if (rateLimitRemaining - maxFollowerPageSize < GITHUB_MIN_REQUEST_THRESHOLD) {
			log.error("팔로워 리스트 조회 중 API Limit 초과 | GitHub User Id : {}, Remaining : {}", githubUserId, rateLimitRemaining);
			throw new GithubApiLimitExceedException();
		}

		List<GithubUser> githubFollowers = new ArrayList<>();

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= maxFollowerPageSize; currentPage++) {

			List<GithubUserResponse> followersResponseList = githubFollowApi.fetchPagedFollowers(accessToken, GITHUB_MAX_QUERY_PAGING_SIZE, currentPage);

			githubFollowers.addAll(
				followersResponseList.stream()
					.filter(userResponse -> "User".equalsIgnoreCase(userResponse.type()))
					.map(userResponse -> new GithubUser(userResponse.id(), userResponse.login(), userResponse.avatarUrl()))
					.toList());

		}
		return githubFollowers;
	}

	public void follow(Long currentUserId, Integer targetUserGithubId) {

		String accessToken = OAuthTokenReader.getGithubAccessToken(currentUserId);
		GithubUserResponse targetUserResponse = githubUserApi.fetchUser(accessToken, targetUserGithubId);

		Response followCommandResponse = githubFollowApi.follow(accessToken, targetUserResponse.login());
		int responseStatus = followCommandResponse.status();

		if (responseStatus == 204) {

			log.info("깃허브 팔로우 요청 성공 | currentUserId : {}, targetUserGithubId : {}, ", currentUserId, targetUserGithubId);
		} else {

			log.warn("깃허브 팔로우 요청 실패 | currentUserId : {}, targetUserId : {}, responseStatus : {}", currentUserId, targetUserGithubId, responseStatus);
			throw new RuntimeException("깃허브 팔로우 요청 처리 실패"); /// TODO 추후 처리
		}
	}

	public void unFollow(Long currentUserId, Integer targetUserGithubId) {

		String accessToken = OAuthTokenReader.getGithubAccessToken(currentUserId);
		GithubUserResponse targetUserResponse = githubUserApi.fetchUser(accessToken, targetUserGithubId);
		Response unFollowCommandResponse = githubFollowApi.unfollow(accessToken, targetUserResponse.login());
		int responseStatus = unFollowCommandResponse.status();

		if (responseStatus == 204) {

			log.info("깃허브 언팔로우 요청 성공 | currentUserId : {}, targetUserGithubId : {}, ", currentUserId,
				targetUserGithubId);
		} else {

			log.warn("깃허브 언팔로우 요청 실패 | currentUserId : {}, targetUserId : {}, responseStatus : {}", currentUserId, targetUserGithubId, responseStatus);
			throw new RuntimeException("깃허브 언팔로우 요청 처리 실패"); /// TODO 추후 처리
		}
	}
}
