package com.moyoy.domain.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import com.moyoy.common.exception.user.UserNotFoundException;
import com.moyoy.domain.common.github.GithubApiLimitChecker;
import com.moyoy.domain.common.github.GithubOAuthTokenReader;
import com.moyoy.domain.user.implement.UserReader;
import com.moyoy.infra.github.feign.GithubFollowClient;
import com.moyoy.infra.github.feign.GithubProfileClient;
import com.moyoy.infra.github.dto.GithubFollowUserResponse;
import com.moyoy.infra.github.dto.GithubProfileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowRelationReader {

	private final GithubApiLimitChecker githubApiLimitChecker;
	private final GithubProfileClient githubProfileClient;
	private final GithubFollowClient githubFollowClient;
	private final GithubOAuthTokenReader githubOAuthTokenReader;
	private final UserReader userReader;

	///  여러 외부 API 호출후 가공해서 만들어진 응답을 캐싱
	@Caching(
		cacheable = @Cacheable(value = "followRelation", key = "#userId", condition = "!#forceSync"),
		put = @CachePut(value = "followRelation", key = "#userId", condition = "#forceSync")
	)
	public GithubFollowRelation findByUserId(Long userId, boolean forceSync) {

		Integer githubUserId = userReader.findById(userId).orElseThrow(UserNotFoundException::new).getGithubUserId();
		String githubAccessToken = githubOAuthTokenReader.getGithubAccessToken(userId);

		githubApiLimitChecker.assertCanGithubRequest(githubAccessToken, githubUserId);

		GithubProfileResponse githubProfileResponse = githubProfileClient.fetchUserProfile(githubAccessToken, githubUserId);
		GithubUserFollowStats followStats = GithubUserFollowStats.from(githubProfileResponse);
		log.info("{}의 팔로워, 팔로잉 페이지 정보 로그 (page size = {}) | followerMaxPage : {} , followingMaxPage : {},", userId, GITHUB_QUERY_PAGING_SIZE, followStats.maxFollowerPageSize(), followStats.maxFollowingPageSize());

		List<GithubFollowUser> githubFollowers = new ArrayList<>();
		List<GithubFollowUser> githubFollowings = new ArrayList<>();

		int maxFollowerPageSize = followStats.maxFollowerPageSize();
		int maxFollowingPageSize = followStats.maxFollowingPageSize();

		long startTime = System.currentTimeMillis(); // 삭제 예정

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= maxFollowerPageSize; currentPage++) {

			List<GithubFollowUserResponse> followersResponseList =
				githubFollowClient.fetchPagedFollowers(GITHUB_QUERY_PAGING_SIZE, currentPage, githubAccessToken);

			githubFollowers.addAll(
				followersResponseList.stream().map(GithubFollowUser::from).toList()
			);

		}

		for (int currentPage = 1; currentPage <= maxFollowingPageSize; currentPage++) {

			List<GithubFollowUserResponse> followingsResponseList =
				githubFollowClient.fetchPagedFollowings(GITHUB_QUERY_PAGING_SIZE, currentPage, githubAccessToken);

			githubFollowings.addAll(
				followingsResponseList.stream().map(GithubFollowUser::from).toList()
			);
		}

		log.info("[개발용 로그] 동기식 API 요청 소요 시간 : {}", System.currentTimeMillis() - startTime); // 삭제 예정

		return GithubFollowRelation.create(userId, githubFollowers, githubFollowings);
	}

}
