package com.moyo.backend.domain.github_follow.data_access;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.moyo.backend.domain.github_follow.implement.GithubFollowUser;

public interface GithubFollowHttpClient {
	ResponseEntity<GithubUserFollowStats> fetchFollowStats(Integer githubUserId, String accessToken);

	List<GithubFollowUser> fetchPagedFollowers(int currentPage, String accessToken);

	List<GithubFollowUser> fetchPagedFollowings(int currentPage, String accessToken);

	GithubFollowUser fetchGithubFollowUserById(Integer githubUserId, String accessToken);

	int follow(String targetUsername, String accessToken);

	int unfollow(String targetUsername, String accessToken);
}
