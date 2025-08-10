package com.moyoy.domain.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import com.moyoy.infra.github.dto.GithubProfileResponse;

public record GithubUserFollowStats(
	int maxFollowingPageSize,
	int maxFollowerPageSize
) {

	/**
	 *   깃허브 기본 페이지는 1부터 시작 (즉, 0 == 1, 같은 페이지)
	 *
	 *   ex) following : 167, follower : 255, PagingSize = 100
	 *       MaxFollowingPage : 167 / 100 + 1 => 2
	 *       MaxFollowerPage : 255 / 100 + 1 => 3
	 */
	public static GithubUserFollowStats from(GithubProfileResponse githubProfileResponse) {

		int maxFollowingPageSize = githubProfileResponse.following() / GITHUB_QUERY_PAGING_SIZE + 1;
		int maxFollowerPageSize = githubProfileResponse.followers() / GITHUB_QUERY_PAGING_SIZE + 1;

		return new GithubUserFollowStats(maxFollowingPageSize, maxFollowerPageSize);
	}
}
