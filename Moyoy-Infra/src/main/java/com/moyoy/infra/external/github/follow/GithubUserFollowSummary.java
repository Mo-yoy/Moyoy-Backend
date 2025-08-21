package com.moyoy.infra.external.github.follow;

import static com.moyoy.common.constant.MoyoConstants.*;

import com.moyoy.infra.external.github.user.GithubUserResponse;

public record GithubUserFollowSummary(
	int maxFollowingPageSize,
	int maxFollowerPageSize) {

	/**
	 *   깃허브 기본 페이지는 1부터 시작 (즉, 0 == 1, 같은 페이지)
	 *
	 *   ex) following : 167, follower : 255, PagingSize = 100
	 *       MaxFollowingPage : 167 / 100 + 1 => 2
	 *       MaxFollowerPage : 255 / 100 + 1 => 3
	 */
	public static GithubUserFollowSummary from(GithubUserResponse githubUserResponse) {

		int maxFollowingPageSize = githubUserResponse.following() / GITHUB_MAX_QUERY_PAGING_SIZE + 1;
		int maxFollowerPageSize = githubUserResponse.followers() / GITHUB_MAX_QUERY_PAGING_SIZE + 1;

		return new GithubUserFollowSummary(maxFollowingPageSize, maxFollowerPageSize);
	}
}
