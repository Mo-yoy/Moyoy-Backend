package com.moyoy.infra.external.github.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.moyoy.domain.follow.GithubUser;
import com.moyoy.domain.support.error.github.GithubApiLimitExceedException;

import com.moyoy.infra.external.github.helper.GithubOAuthTokenReader;
import com.moyoy.infra.external.github.user.GithubUserFeignClient;
import com.moyoy.infra.external.github.user.GithubUserResponse;

import feign.Response;
import feign.Util;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowClientImpl implements GithubFollowClient {

	private final GithubFollowFeignClient githubFollowFeignClient;
	private final GithubUserFeignClient githubUserFeignClient;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final ObjectMapper objectMapper;

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "followFetchFallBack")
	public List<GithubUser> fetchFollowings(Long userId, Integer githubUserId) {

		String accessToken = githubOAuthTokenReader.getGithubAccessToken(userId);
		Response userRawResponse = githubUserFeignClient.fetchUserRawResponse(accessToken, githubUserId);
		GithubUserResponse githubUserResponse = mapBody(userRawResponse, GithubUserResponse.class);
		int rateLimitRemaining = getLimitRemaining(userRawResponse);

		int maxFollowingPageSize = githubUserResponse.following() / GITHUB_MAX_QUERY_PAGING_SIZE + 1;
		log.info("{}의 깃허브 팔로잉 리스트 조회 요청 로그 (page size = {}) | followingMaxPage : {},", userId, GITHUB_MAX_QUERY_PAGING_SIZE, maxFollowingPageSize);

		if (rateLimitRemaining - maxFollowingPageSize < GITHUB_MIN_REQUEST_THRESHOLD) {
			log.error("팔로잉 리스트 조회 중 API Limit 초과 | GitHub User Id : {}, Remaining : {}", githubUserId, rateLimitRemaining);
			throw new GithubApiLimitExceedException();
		}

		List<GithubUser> githubFollowings = new ArrayList<>();

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= maxFollowingPageSize; currentPage++) {

			List<GithubUserResponse> followingsResponseList = githubFollowFeignClient.fetchPagedFollowings(accessToken, GITHUB_MAX_QUERY_PAGING_SIZE, currentPage);

			githubFollowings.addAll(
				followingsResponseList.stream()
					.map(userResponse -> new GithubUser(userResponse.id(), userResponse.login(), userResponse.avatarUrl()))
					.toList());
		}

		return githubFollowings;
	}

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "followFetchFallBack")
	public List<GithubUser> fetchFollowers(Long userId, Integer githubUserId) {

		String accessToken = githubOAuthTokenReader.getGithubAccessToken(userId);
		Response userRawResponse = githubUserFeignClient.fetchUserRawResponse(accessToken, githubUserId);
		GithubUserResponse githubUserResponse = mapBody(userRawResponse, GithubUserResponse.class);
		int rateLimitRemaining = getLimitRemaining(userRawResponse);

		int maxFollowerPageSize = githubUserResponse.followers() / GITHUB_MAX_QUERY_PAGING_SIZE + 1;
		log.info("{}의 깃허브 팔로워 리스트 조회 요청 로그 (page size = {}) | followerMaxPage : {},", userId, GITHUB_MAX_QUERY_PAGING_SIZE, maxFollowerPageSize);

		if (rateLimitRemaining - maxFollowerPageSize < GITHUB_MIN_REQUEST_THRESHOLD) {
			log.error("팔로워 리스트 조회 중 API Limit 초과 | GitHub User Id : {}, Remaining : {}", githubUserId, rateLimitRemaining);
			throw new GithubApiLimitExceedException();
		}

		List<GithubUser> githubFollowers = new ArrayList<>();

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= maxFollowerPageSize; currentPage++) {

			List<GithubUserResponse> followersResponseList = githubFollowFeignClient.fetchPagedFollowers(accessToken, GITHUB_MAX_QUERY_PAGING_SIZE, currentPage);

			githubFollowers.addAll(
				followersResponseList.stream()
					.map(userResponse -> new GithubUser(userResponse.id(), userResponse.login(), userResponse.avatarUrl()))
					.toList());

		}
		return githubFollowers;
	}

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "followCommandFallback")
	public void follow(Long currentUserId, Integer targetUserGithubId) {

		String accessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);
		GithubUserResponse targetUserResponse = githubUserFeignClient.fetchUser(accessToken, targetUserGithubId);
		Response followCommandResponse = githubFollowFeignClient.follow(accessToken, targetUserResponse.login());
		int responseStatus = followCommandResponse.status();

		if (responseStatus == 204) {

			log.info("깃허브 팔로우 요청 성공 | currentUserId : {}, targetUserGithubId : {}, ", currentUserId, targetUserGithubId);
		} else {

			log.warn("깃허브 팔로우 요청 실패 | currentUserId : {}, targetUserId : {}, responseStatus : {}", currentUserId, targetUserGithubId, responseStatus);
			throw new RuntimeException("깃허브 팔로우 요청 처리 실패"); /// TODO 추후 처리
		}
	}

	@Override
	@CircuitBreaker(name = "githubApi", fallbackMethod = "followCommandFallback")
	public void unFollow(Long currentUserId, Integer targetUserGithubId) {

		String accessToken = githubOAuthTokenReader.getGithubAccessToken(currentUserId);
		GithubUserResponse targetUserResponse = githubUserFeignClient.fetchUser(accessToken, targetUserGithubId);
		Response unFollowCommandResponse = githubFollowFeignClient.unfollow(accessToken, targetUserResponse.login());
		int responseStatus = unFollowCommandResponse.status();

		if (responseStatus == 204) {

			log.info("깃허브 언팔로우 요청 성공 | currentUserId : {}, targetUserGithubId : {}, ", currentUserId,
				targetUserGithubId);
		} else {

			log.warn("깃허브 언팔로우 요청 실패 | currentUserId : {}, targetUserId : {}, responseStatus : {}", currentUserId, targetUserGithubId, responseStatus);
			throw new RuntimeException("깃허브 언팔로우 요청 처리 실패"); /// TODO 추후 처리
		}
	}


	private void followFetchFallBack(Long userId, Integer githubUserId, Throwable throwable) {
		if(throwable instanceof CallNotPermittedException){

		}
		/// TODO : 에러코드 회의 후 처리
	}

	private void followCommandFallback(Long userId, Integer githubUserId, Throwable throwable) {
		if(throwable instanceof CallNotPermittedException){

		}
		/// TODO : 에러코드 회의 후 처리
	}

	private int getLimitRemaining(Response response) {

		String rateLimitHeader = response.headers()
			.getOrDefault(GITHUB_RATE_LIMIT_HEADER, List.of())
			.stream()
			.findFirst()
			.orElse("0");

		return Integer.parseInt(rateLimitHeader);
	}

	private <T> T mapBody(Response response, Class<T> clazz) {
		try (Reader reader = response.body().asReader(StandardCharsets.UTF_8)) {
			String body = Util.toString(reader);
			return objectMapper.readValue(body, clazz);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to parse GitHub API response", e);
		}
	}
}
