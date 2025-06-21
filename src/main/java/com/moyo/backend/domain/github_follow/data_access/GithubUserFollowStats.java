package com.moyo.backend.domain.github_follow.data_access;

import static com.moyo.backend.common.constant.MoyoConstants.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubUserFollowStats (
	int maxFollowingPageSize,
	int maxFollowerPageSize
){

	/**
	 *   깃허브 기본 페이지는 1부터 시작 (즉, 0 == 1, 같은 페이지)
	 *
	 *   ex) following : 167, follower : 255, PagingSize = 100
	 *       MaxFollowingPage : 167 / 100 + 1 => 2
	 *       MaxFollowerPage : 255 / 100 + 1 => 3
	 */
	public GithubUserFollowStats(
		@JsonProperty("following") Integer followingCnt,
		@JsonProperty("followers") Integer followerCnt) {

		this(
			followingCnt / GITHUB_FOLLOW_QUERY_PAGING_SIZE + 1,
			followerCnt / GITHUB_FOLLOW_QUERY_PAGING_SIZE + 1
		);
	}

	public int getTotalRequestCnt() {
		return maxFollowingPageSize + maxFollowerPageSize;
	}
}
