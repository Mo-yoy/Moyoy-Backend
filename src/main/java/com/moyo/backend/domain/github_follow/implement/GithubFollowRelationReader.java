package com.moyo.backend.domain.github_follow.implement;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.moyo.backend.common.exception.github_follow.GithubRateLimitExceedException;
import com.moyo.backend.common.exception.user.UserNotFoundException;
import com.moyo.backend.common.implement.GithubOAuthTokenReader;
import com.moyo.backend.domain.github_follow.data_access.GithubFollowHttpClient;
import com.moyo.backend.domain.github_follow.data_access.GithubUserFollowStats;
import com.moyo.backend.domain.user.implement.UserReader;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowRelationReader {

	private final GithubFollowHttpClient githubFollowHttpClient;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final UserReader userReader;

	@Caching(cacheable = @Cacheable(value = "followRelation", key = "#userId", condition = "!#forceSync"), put = @CachePut(value = "followRelation", key = "#userId", condition = "#forceSync"))
	public GithubFollowRelation findByUserId(Long userId, boolean forceSync) {

		Integer githubUserId = userReader.findById(userId).orElseThrow(UserNotFoundException::new).getGithubUserId();
		String githubAccessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

		ResponseEntity<GithubUserFollowStats> response = githubFollowHttpClient.fetchFollowStats(githubUserId, githubAccessToken);

		GithubUserFollowStats followStats = response.getBody();
		int remainingRequestCnt = Integer.parseInt(response.getHeaders().get("X-RateLimit-Remaining").getFirst());
		int requiredRequestCnt = followStats.getTotalRequestCnt();
		if (remainingRequestCnt < requiredRequestCnt + GITHUB_MIN_REQUEST_THRESHOLD)
			throw new GithubRateLimitExceedException();

		log.info("{}의 팔로워, 팔로잉 페이지 정보 로그 (page size = {}) | followerMaxPage : {} , followingMaxPage : {}, 남은 요청 가능 횟수 : {}",
			userId, GITHUB_FOLLOW_QUERY_PAGING_SIZE, followStats.maxFollowerPageSize(), followStats.maxFollowingPageSize(), remainingRequestCnt);

		List<GithubFollowUser> githubFollowers = new ArrayList<>();
		List<GithubFollowUser> githubFollowings = new ArrayList<>();

		int maxFollowerPageSize = followStats.maxFollowerPageSize();
		int maxFollowingPageSize = followStats.maxFollowingPageSize();

		long startTime = System.currentTimeMillis(); // 삭제 예정

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= maxFollowerPageSize; currentPage++) {

			githubFollowers.addAll(githubFollowHttpClient.fetchPagedFollowers(currentPage, githubAccessToken));
		}

		for (int currentPage = 1; currentPage <= maxFollowingPageSize; currentPage++) {

			githubFollowings.addAll(githubFollowHttpClient.fetchPagedFollowings(currentPage, githubAccessToken));
		}

		log.info("[개발용 로그] 동기식 API 요청 소요 시간 : {}", System.currentTimeMillis() - startTime); // 삭제 예정

		return GithubFollowRelation.create(userId, githubFollowers, githubFollowings);
	}

}
