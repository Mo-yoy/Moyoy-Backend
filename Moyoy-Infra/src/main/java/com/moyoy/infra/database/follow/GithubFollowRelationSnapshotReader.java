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

import com.moyoy.infra.external.github.follow.GithubUserFollowSummary;
import com.moyoy.infra.external.github.helper.GithubApiLimitChecker;
import com.moyoy.infra.external.github.user.GithubFollowUserResponse;
import com.moyoy.domain.follow.GithubUser;
import com.moyoy.infra.external.github.user.GithubUserResponse;
import com.moyoy.infra.external.github.follow.GithubFollowFeignClient;
import com.moyoy.infra.external.github.user.GithubUserFeignClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubFollowRelationSnapshotReader {

	private final GithubApiLimitChecker githubApiLimitChecker;
	private final GithubUserFeignClient githubUserFeignClient;
	private final GithubFollowFeignClient githubFollowFeignClient;

	@Caching(cacheable = @Cacheable(value = "followRelation", key = "#userId", condition = "!#forceSync"), put = @CachePut(value = "followRelation", key = "#userId", condition = "#forceSync"))
	public GithubFollowRelationSnapshot loadFollowRelationSnapshot(Long userId, boolean forceSync, Integer githubUserId, String accessToken) {

		githubApiLimitChecker.assertCanGithubRequest(accessToken, githubUserId);

		GithubUserResponse githubUserResponse = githubUserFeignClient.fetchUser(accessToken, githubUserId);
		GithubUserFollowSummary followStats = GithubUserFollowSummary.from(githubUserResponse);

		log.info("{}의 팔로워, 팔로잉 페이지 정보 로그 (page size = {}) | followerMaxPage : {} , followingMaxPage : {},", userId, GITHUB_MAX_QUERY_PAGING_SIZE, followStats.maxFollowerPageSize(),
			followStats.maxFollowingPageSize());

		List<GithubUser> githubFollowers = new ArrayList<>();
		List<GithubUser> githubFollowings = new ArrayList<>();

		int maxFollowerPageSize = followStats.maxFollowerPageSize();
		int maxFollowingPageSize = followStats.maxFollowingPageSize();

		long startTime = System.currentTimeMillis(); /// TODO 삭제 예정

		// 추후 비동기로 개선할 성능 장애 지점, 깃허브 페이지는 1부터 시작
		for (int currentPage = 1; currentPage <= maxFollowerPageSize; currentPage++) {

			List<GithubFollowUserResponse> followersResponseList = githubFollowFeignClient.fetchPagedFollowers(GITHUB_MAX_QUERY_PAGING_SIZE, currentPage, accessToken);

			githubFollowers.addAll(
				followersResponseList.stream()
					.map(GithubUser::from)
					.toList());

		}

		for (int currentPage = 1; currentPage <= maxFollowingPageSize; currentPage++) {

			List<GithubFollowUserResponse> followingsResponseList = githubFollowFeignClient.fetchPagedFollowings(GITHUB_MAX_QUERY_PAGING_SIZE, currentPage, accessToken);

			githubFollowings.addAll(
				followingsResponseList.stream()
					.map(GithubUser::from)
					.toList());
		}
		log.info("[개발용 로그] 동기식 API 요청 소요 시간 : {}", System.currentTimeMillis() - startTime); /// TODO 삭제 예정

		return GithubFollowRelationSnapshot.create(userId, githubFollowers, githubFollowings);
	}

}
