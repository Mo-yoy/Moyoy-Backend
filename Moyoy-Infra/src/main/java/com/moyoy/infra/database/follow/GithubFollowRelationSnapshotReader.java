package com.moyoy.infra.database.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import com.moyoy.infra.external.github.common.GithubApiLimitChecker;
import com.moyoy.infra.external.github.dto.GithubFollowUserResponse;
import com.moyoy.infra.external.github.dto.GithubProfileResponse;
import com.moyoy.infra.external.github.feign.GithubFollowClient;
import com.moyoy.infra.external.github.feign.GithubProfileClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowRelationSnapshotReader {

	private final GithubApiLimitChecker githubApiLimitChecker;
	private final GithubProfileClient githubProfileClient;
	private final GithubFollowClient githubFollowClient;

	@Caching(cacheable = @Cacheable(value = "followRelation", key = "#userId", condition = "!#forceSync"), put = @CachePut(value = "followRelation", key = "#userId", condition = "#forceSync"))
	public GithubFollowRelationSnapshot loadFollowRelationSnapshot(Long userId, boolean forceSync, Integer githubUserId, String accessToken) {

		githubApiLimitChecker.assertCanGithubRequest(accessToken, githubUserId);

		GithubProfileResponse githubProfileResponse = githubProfileClient.fetchUserProfile(accessToken, githubUserId);
		GithubUserFollowStats followStats = GithubUserFollowStats.from(githubProfileResponse);

		log.info("{}의 팔로워, 팔로잉 페이지 정보 로그 (page size = {}) | followerMaxPage : {} , followingMaxPage : {},", userId, GITHUB_MAX_QUERY_PAGING_SIZE, followStats.maxFollowerPageSize(),
			followStats.maxFollowingPageSize());

		List<GithubFollowUser> githubFollowers = new ArrayList<>();
		List<GithubFollowUser> githubFollowings = new ArrayList<>();

		int maxFollowerPageSize = followStats.maxFollowerPageSize();
		int maxFollowingPageSize = followStats.maxFollowingPageSize();

		long startTime = System.currentTimeMillis(); /// TODO 삭제 예정

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= maxFollowerPageSize; currentPage++) {

			List<GithubFollowUserResponse> followersResponseList = githubFollowClient.fetchPagedFollowers(GITHUB_MAX_QUERY_PAGING_SIZE, currentPage, accessToken);

			githubFollowers.addAll(
				followersResponseList.stream()
					.map(GithubFollowUser::from)
					.toList());

		}

		for (int currentPage = 1; currentPage <= maxFollowingPageSize; currentPage++) {

			List<GithubFollowUserResponse> followingsResponseList = githubFollowClient.fetchPagedFollowings(GITHUB_MAX_QUERY_PAGING_SIZE, currentPage, accessToken);

			githubFollowings.addAll(
				followingsResponseList.stream()
					.map(GithubFollowUser::from)
					.toList());
		}
		log.info("[개발용 로그] 동기식 API 요청 소요 시간 : {}", System.currentTimeMillis() - startTime); /// TODO 삭제 예정

		return GithubFollowRelationSnapshot.create(userId, githubFollowers, githubFollowings);
	}

}
